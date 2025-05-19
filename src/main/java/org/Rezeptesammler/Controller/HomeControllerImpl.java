package org.Rezeptesammler.Controller;

import org.Rezeptesammler.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerImpl implements HomeController{

    @Autowired
    RecipeService recipeService;

    @Override
    @GetMapping("/")
    public String getHome(Model model) {

        return "index.html";
    }

    @GetMapping("/datastorage")
    public String getDatastorage(Model model){

        model.addAttribute("recipes",recipeService.getAllRecipes());

        return "datastorage.html";
    }

}
