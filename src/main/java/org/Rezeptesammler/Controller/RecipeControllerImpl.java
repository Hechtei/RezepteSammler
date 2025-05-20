package org.Rezeptesammler.Controller;

import org.Rezeptesammler.Model.Recipe;
import org.Rezeptesammler.Service.CouchDBService;
import org.Rezeptesammler.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Controller
public class RecipeControllerImpl implements RecipeController{

    @Autowired
    RecipeService recipeService;

    @Autowired
    CouchDBService couchDBService;

    @GetMapping("/recipe")
    public String getRecipe(Model model){

        return "recipe.html";
    }

    @GetMapping("/recipe/{id}")
    public String getRecipe(@PathVariable("id") String id, Model model) {
        model.addAttribute("recipe", couchDBService.getRecipeBy_Id(id));
        return "getRecipe.html";
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
