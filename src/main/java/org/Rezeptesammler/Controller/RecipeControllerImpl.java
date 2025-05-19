package org.Rezeptesammler.Controller;

import org.Rezeptesammler.Model.Recipe;
import org.Rezeptesammler.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;


@Controller
public class RecipeControllerImpl implements RecipeController{

    @Autowired
    RecipeService recipeService;

    @GetMapping("/recipe")
    public String getRecipe(Model model){

        return "recipe.html";
    }

    @Override
    @PostMapping("/recipe/save")
    public String saveRecipe(@RequestParam String name, @RequestParam String link, Model model) {

        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setLink(link);

        try {
            recipeService.saveRecipe(recipe);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/recipe";
    }
}
