package org.Rezeptesammler.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerImpl implements HomeController{


    @Override
    @GetMapping("/")
    public String getHome(Model model) {
        return "index.html";
    }


}
