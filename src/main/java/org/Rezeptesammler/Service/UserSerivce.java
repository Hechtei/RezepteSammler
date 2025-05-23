package org.Rezeptesammler.Service;

import org.Rezeptesammler.Model.User;

public interface UserSerivce {


    public void registerUser(User user);

    public boolean checkRegister(User user);

    public User loginUser(User user);

    public boolean checkLogin(User user);
}
