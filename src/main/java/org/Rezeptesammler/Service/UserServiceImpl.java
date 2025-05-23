package org.Rezeptesammler.Service;

import com.google.gson.JsonObject;
import org.Rezeptesammler.Model.User;
import org.lightcouch.CouchDbClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserServiceImpl implements UserSerivce {

    private static final CouchDbClient dbUserClient = new CouchDbClient("couchdb_user.properties");

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void registerUser(User user) {
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("GMT+02:00")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString());
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        dbUserClient.save(user);
    }

    @Override
    public boolean checkRegister(User user) {
        String usernameToCheck = user.getUsername();
        List<JsonObject> result = dbUserClient.view("users/checkUsername")
                .key(usernameToCheck)
                .includeDocs(false)
                .query(JsonObject.class);

        return !result.isEmpty();
    }

    @Override
    public boolean checkLogin(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        List<User> result = dbUserClient.view("users/checkUsername")
                .key(username)
                .includeDocs(true)
                .query(User.class);

        if (result.isEmpty()) {
            return false;
        }

        User storedUser = result.get(0);
        return passwordEncoder.matches(password, storedUser.getPassword());
    }

    @Override
    public User loginUser(User user) {
        String username = user.getUsername();

        List<User> result = dbUserClient.view("users/checkUsername")
                .key(username)
                .includeDocs(true)
                .query(User.class);

        if (result.isEmpty()) {
            return null;
        }

        User storedUser = result.get(0);

        if (passwordEncoder.matches(user.getPassword(), storedUser.getPassword())) {
            return storedUser;
        }

        return null;
    }

}
