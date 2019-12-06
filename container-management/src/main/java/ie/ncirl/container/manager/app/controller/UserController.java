package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.dto.PageData;
import ie.ncirl.container.manager.app.dto.UserDTO;
import ie.ncirl.container.manager.app.service.UserService;
import ie.ncirl.container.manager.app.util.PageUtil;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
public class UserController {

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
    public String showUserList(Model model, @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<User> usersPage = userService.findAll(PageRequest.of(currentPage - 1, pageSize));
        List<User> users = usersPage.stream()
                .filter(user -> !user.getUsername().equals(User.ROOT_USERNAME))
                .collect(Collectors.toList());

        model.addAttribute("users", users);
        PageData pageData = PageUtil.getPageData(usersPage);
        model.addAttribute("pageNumbers", pageData.getPageNumbers());
        model.addAttribute("currPage", pageData.getCurrPage());
        model.addAttribute("totalPages", pageData.getTotalPages());
        return "user/list";
    }

    @PostMapping("/user/register")
    public String registration(@ModelAttribute("user") UserDTO user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        userService.save(user);
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
