package org.Rezeptesammler.Service;

import org.Rezeptesammler.Model.Recipe;

import java.util.List;

public interface CouchDBService {

    public List<Recipe> getAllRecipes();

    public Recipe getRecipeBy_Id(String _id);


}
