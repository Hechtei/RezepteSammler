package org.Rezeptesammler.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Rezeptesammler.Model.Recipe;
import org.lightcouch.CouchDbClient;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CouchDBServiceImpl implements CouchDBService{

    private static final CouchDbClient dbClient = new CouchDbClient("couchdb_recipe.properties");
    private static final String DOWNLOAD_DIR = "downloads";
    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<Recipe> getAllRecipes() {
        return dbClient.view("Recipes/allRecipes").includeDocs(true).query(Recipe.class);
    }

    @Override
    public Recipe getRecipeBy_Id(String _id) {
        return dbClient.find(Recipe.class, _id);
    }


}
