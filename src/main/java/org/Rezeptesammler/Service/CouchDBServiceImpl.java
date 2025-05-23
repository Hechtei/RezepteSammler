package org.Rezeptesammler.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.Rezeptesammler.Model.Recipe;
import org.lightcouch.CouchDbClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
@Service
public class CouchDBServiceImpl implements CouchDBService{

    private static final CouchDbClient dbClient = new CouchDbClient("couchdb_recipe.properties");
    private static final String DOWNLOAD_DIR = "downloads";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public List<Recipe> getAllRecipes() {
        return dbClient.view("Recipes/allRecipes").includeDocs(true).query(Recipe.class);
    }

    public int countAllRecipes() {
            List<JsonObject> result = dbClient.view("Recipes/countAllRecipes").includeDocs(false).query(JsonObject.class);
            if(!result.isEmpty()) {
                return result.get(0).get("value").getAsInt();
            }
            return 0 ;
        }

    public int countVegiRecipes() {
            List<JsonObject> result = dbClient.view("Recipes/countVegiRecipes").includeDocs(false).query(JsonObject.class);
        if(!result.isEmpty()) {
            return result.get(0).get("value").getAsInt();
        }
        return 0 ;
    }

    public int countVeganRecipes() {
        List<JsonObject> result = dbClient.view("Recipes/countVeganRecipes").includeDocs(false).query(JsonObject.class);
        if(!result.isEmpty()) {
            return result.get(0).get("value").getAsInt();
        }
        return 0 ;
    }


    @Override
    public Recipe getRecipeBy_Id(String _id) {
        return dbClient.find(Recipe.class, _id);
    }



    @Override
    public Recipe getRandomRecipe() {
        // 1. Zufällige UUID generieren
        String randomUUID = UUID.randomUUID().toString();

        // 2. Mit startkey suchen, limit 1, um nächstes Dokument >= randomUUID zu bekommen
        List<Recipe> results = dbClient.view("Recipes/allRecipes")
                .startKey(randomUUID)
                .limit(1)
                .includeDocs(true)
                .query(Recipe.class);

        // 3. Wenn nichts gefunden, wrap-around (das erste Dokument)
        if (results.isEmpty()) {
            results = dbClient.view("Recipes/allRecipes")
                    .limit(1)
                    .includeDocs(true)
                    .query(Recipe.class);
        }

        // 4. Rückgabe des gefundenen Rezepts oder null
        return results.isEmpty() ? null : results.get(0);
    }


}
