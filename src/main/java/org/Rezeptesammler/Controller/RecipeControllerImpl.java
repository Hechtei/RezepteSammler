package org.Rezeptesammler.Controller;

import jakarta.servlet.http.HttpSession;
import org.Rezeptesammler.Model.Recipe;
import org.Rezeptesammler.Model.User;
import org.Rezeptesammler.Service.CouchDBService;
import org.Rezeptesammler.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;


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

    @GetMapping("/recipe/update/{id}")
    public String getUpdateRecipe(@PathVariable("id") String id, Model model) {
        model.addAttribute("recipe", couchDBService.getRecipeBy_Id(id));
        return "editRecipe.html";
    }

    @PostMapping("/recipe/update/{id}")
    public String postUpdateRecipe(@PathVariable("id") String id, @ModelAttribute Recipe recipe, RedirectAttributes redirectAttributes) throws IllegalAccessException {

        Recipe currentrecipe = couchDBService.getRecipeBy_Id(id);

        Field[] fields = Recipe.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);  // Zugriff auf private Felder erlauben
            Object value = field.get(recipe);
            if (value != null) {
                field.set(currentrecipe, value);
            }
        }

        recipeService.updateRecipe(currentrecipe);

        redirectAttributes.addFlashAttribute("message", "Rezept erfolgreich aktualisiert!");
        return "redirect:/recipe/"+id;
    }





    @Override
    @PostMapping("/recipe/save")
    public String saveRecipe(@RequestParam String name, @RequestParam String link, Model model, HttpSession session) {

        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setLink(link);

        try {
            recipeService.saveRecipe(recipe, ((User) session.getAttribute("user")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/recipe";
    }

    @PostMapping("/recipe/delete/{id}")
    public String deleteRecipe(@PathVariable("id") String id, RedirectAttributes redirectAttributes){

        if(recipeService.delteRecipe(id)){
            redirectAttributes.addFlashAttribute("success","Rezept mit id: "+id+" gelöscht");
        } else{
            redirectAttributes.addFlashAttribute("error","Löschen fehlgeschlagen");
        }
        return "redirect:/datastorage";
    }
}
