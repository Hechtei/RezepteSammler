package org.Rezeptesammler.Service;

import org.Rezeptesammler.Model.Recipe;

import java.util.List;

public interface CouchDBService {

    public List<Recipe> getAllRecipes();

    public int countAllRecipes();

    public int countVegiRecipes();

    public int countVeganRecipes();

    public Recipe getRecipeBy_Id(String _id);

    public Recipe getRandomRecipe();
}
