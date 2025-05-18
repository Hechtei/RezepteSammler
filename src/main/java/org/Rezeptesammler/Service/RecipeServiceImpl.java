package org.Rezeptesammler.Service;

import org.Rezeptesammler.Model.Recipe;
import org.lightcouch.CouchDbClient;

public class RecipeServiceImpl implements RecipeService {

    private static CouchDbClient dbClient = new CouchDbClient("couchdb_recipe.properties");


    public void saveRecipe(Recipe recipe){


    }

}
