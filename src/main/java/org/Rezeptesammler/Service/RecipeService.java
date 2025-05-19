package org.Rezeptesammler.Service;

import org.Rezeptesammler.Model.Recipe;

import java.io.IOException;
import java.util.List;

public interface RecipeService {

    public void saveRecipe(Recipe recipe) throws IOException, InterruptedException;

    public List<Recipe> getAllRecipes();
}
