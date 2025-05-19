package org.Rezeptesammler.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.Rezeptesammler.Model.Recipe;
import org.lightcouch.CouchDbClient;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeServiceImpl implements RecipeService {

    private static CouchDbClient dbClient = new CouchDbClient("couchdb_recipe.properties");

    private static final String DOWNLOAD_DIR = "downloads";

    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<Recipe> getAllRecipes(){
        return dbClient.view("Recipes/allRecipes").includeDocs(true).query(Recipe.class);
    }

    @Override
    public void saveRecipe(Recipe recipe) throws IOException, InterruptedException {
        File dir = new File(DOWNLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        recipe.set_id(UUID.randomUUID().toString());
        // Rezept-ID als Basis für den Dateinamen
        String baseFilename = recipe.get_id();

        // yt-dlp mit festem Dateinamen
        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "-P", DOWNLOAD_DIR,
                "--write-info-json",
                "--write-thumbnail",
                "-o", baseFilename,  // Dateiname ohne Erweiterung, yt-dlp hängt automatisch Endungen an
                recipe.getLink()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("yt-dlp fehlgeschlagen mit Code: " + exitCode);
        }

        // Jetzt die Info-JSON Datei mit dem Rezept-ID-Namen laden
        File jsonFile = new File(DOWNLOAD_DIR, baseFilename + ".info.json");

        if (jsonFile.exists()) {
            JsonNode root = objectMapper.readTree(jsonFile);
            String description = root.path("description").asText(null);
            if (description != null) {
                recipe.setCaption(description);
            }
        }

        File thumbnail = new File(DOWNLOAD_DIR, baseFilename + ".jpg");
        if(thumbnail.exists()){
            recipe.setThumbnail(thumbnail.getAbsolutePath());
        }

        dbClient.save(recipe);
    }

}
