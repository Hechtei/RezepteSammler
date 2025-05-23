package org.Rezeptesammler.Service;

import org.Rezeptesammler.Model.Recipe;
import org.Rezeptesammler.Model.User;

import java.io.IOException;
import java.util.List;

public interface RecipeService {

    public void saveRecipe(Recipe recipe, User user) throws IOException, InterruptedException;

    public boolean delteRecipe(String id);

    public void updateRecipe(Recipe recipe);
}
