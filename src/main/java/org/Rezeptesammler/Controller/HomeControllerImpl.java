package org.Rezeptesammler.Controller;

import jakarta.servlet.http.HttpSession;
import org.Rezeptesammler.Model.User;
import org.Rezeptesammler.Service.CouchDBService;
import org.Rezeptesammler.Service.RecipeService;
import org.Rezeptesammler.Service.UserSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeControllerImpl implements HomeController{

    @Autowired
    RecipeService recipeService;

    @Autowired
    CouchDBService couchDBService;

    @Autowired
    UserSerivce userSerivce;

    @Override
    @GetMapping("/")
    public String getHome(Model model) {

        model.addAttribute("recipe", couchDBService.getRandomRecipe());
        model.addAttribute("allRecipes",couchDBService.countAllRecipes());
        model.addAttribute("vegiRecipes",couchDBService.countVegiRecipes());
        model.addAttribute("veganRecipes",couchDBService.countVeganRecipes());
        return "index.html";
    }

    @Override
    @GetMapping("/login")
    public String getLogin(Model model) {
        return "login.html";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, RedirectAttributes model, HttpSession session) {
        if (userSerivce.checkLogin(user)) {
            User loggedInUser = userSerivce.loginUser(user);
            session.setAttribute("user", loggedInUser);
            return "redirect:/";
        } else {
            model.addFlashAttribute("error", "Benutzername oder Passwort falsch.");
            return "redirect:/login";
        }
    }


    @Override
    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("user",new User());
        return "register.html";
    }

    @Override
    @PostMapping("/register")
    public String postRegister(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        if(userSerivce.checkRegister(user)){
            redirectAttributes.addFlashAttribute("error","Username "+user.getUsername()+" bereits belegt!");
            return "redirect:/register";
        }
        userSerivce.registerUser(user);
        redirectAttributes.addFlashAttribute("message","User "+user.getUsername()+" erfolgreich angelegt");
        return "redirect:/login";
    }

    @Override
    @PostMapping("/logout")
    public String logout(HttpSession session, Model model) {
        return "redirect:/login";
    }

    @GetMapping("/datastorage")
    public String getDatastorage(Model model){

        model.addAttribute("recipes",couchDBService.getAllRecipes());

        return "datastorage.html";
    }

}
