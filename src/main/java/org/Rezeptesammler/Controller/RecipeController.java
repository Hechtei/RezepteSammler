package org.Rezeptesammler.Controller;

import org.Rezeptesammler.Model.Recipe;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


public interface RecipeController {

    @PostMapping("/save")
    public String saveRecipe(@RequestParam String name, @RequestParam String link, Model model);
    @GetMapping("/recipe")
    public String getRecipe(Model model);

    public String postUpdateRecipe(@PathVariable("id") String id, @ModelAttribute Recipe recipe, RedirectAttributes redirectAttributes) throws IllegalAccessException ;



    }
