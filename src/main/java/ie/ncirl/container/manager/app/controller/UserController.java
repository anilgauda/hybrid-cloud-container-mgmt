package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.dto.UserDTO;
import ie.ncirl.container.manager.app.service.SecurityService;
import ie.ncirl.container.manager.app.service.UserService;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserUtil userUtil;

    @GetMapping("/user/register")
    public String registration(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/register";
    }

    @GetMapping("/user/list")
    public String showUserList(Model model) {
        List<User> users = userService.findAll().stream()
                .filter(user -> !user.getUsername().equals(User.ROOT_USERNAME))
                .sorted(Comparator.comparing(User::getUsername))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "user/list";
    }

    @PostMapping("/user/register")
    public String registration(@ModelAttribute("user") UserDTO user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        userService.save(user);
        securityService.autoLogin(user.getUsername(), user.getPassword());
        return "redirect:/";
    }

    @PostMapping("/user/{id}/role/{role}")
    public String modifyRole(@PathVariable("id") Long id, @PathVariable("role") String role) {
        if (!userUtil.canModifyRole()) {
            log.error(String.format("Invalid request from user %d for modifying role %s", id, role));
            return "redirect:/user/list";
        }
        log.info(String.format("Update user %d with new role %s", id, role));
        userService.updateRole(id, role);
        return "redirect:/user/list";
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
