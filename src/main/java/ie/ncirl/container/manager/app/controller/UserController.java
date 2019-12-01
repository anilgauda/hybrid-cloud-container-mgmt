package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.service.SecurityService;
import ie.ncirl.container.manager.app.service.UserService;
import ie.ncirl.container.manager.common.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Login code taken referred from: https://www.baeldung.com/spring-security-login
 */
@Slf4j
@Controller
public class UserController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/register")
    public String registration(Model model) {
        model.addAttribute("user", new User());

        return "user/register";
    }

    @PostMapping("/user/register")
    public String registration(@ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        String password = user.getPassword();
        userService.save(user);
        securityService.autoLogin(user.getUsername(), password);
        return "redirect:/";
    }

    @GetMapping(value = "/user/login")
    public String showLoginPage(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        model.addAttribute("user", new User());
        return "user/login";
    }

}
