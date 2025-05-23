package org.Rezeptesammler.Controller;

import jakarta.servlet.http.HttpSession;
import org.Rezeptesammler.Model.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface HomeController {

    public String getHome(Model model);

    public String getLogin(Model model);

    public String getRegister(Model model);

    public String logout(HttpSession session, Model model);

    public String postRegister(@ModelAttribute User user, RedirectAttributes redirectAttributes);
}
