package org.Rezeptesammler.Controller;

import org.Rezeptesammler.Model.Recipe;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface RecipeController {

    @PostMapping("/save")
    public String saveRecipe(@RequestParam String name, @RequestParam String link, Model model);
    @GetMapping("/recipe")
    public String getRecipe(Model model);

}
