package org.Rezeptesammler.Controller;

import org.Rezeptesammler.Service.CouchDBService;
import org.Rezeptesammler.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerImpl implements HomeController{

    @Autowired
    RecipeService recipeService;

    @Autowired
    CouchDBService couchDBService;

    @Override
    @GetMapping("/")
    public String getHome(Model model) {

        model.addAttribute("recipe", couchDBService.getRandomRecipe());
        model.addAttribute("allRecipes",couchDBService.countAllRecipes());
        model.addAttribute("vegiRecipes",couchDBService.countVegiRecipes());
        model.addAttribute("veganRecipes",couchDBService.countVeganRecipes());

        return "index.html";
    }

    @GetMapping("/datastorage")
    public String getDatastorage(Model model){

        model.addAttribute("recipes",couchDBService.getAllRecipes());

        return "datastorage.html";
    }

}
