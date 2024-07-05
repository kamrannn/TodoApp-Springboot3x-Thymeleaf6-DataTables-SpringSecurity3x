package com.app.todo.controller;

import com.app.todo.model.User;
import com.app.todo.service.UserService;
import com.app.todo.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public String viewAllUsers(Model model, @ModelAttribute(Constants.MESSAGE_ATTRIBUTE) String message) {
        List<User> users = userService.findAllUsers();
        User user = userService.utils.getLoggedInUser();
        if (user != null) {
            users.remove(user);
        }
        model.addAttribute("users", users);
        model.addAttribute(Constants.MESSAGE_ATTRIBUTE, Objects.requireNonNullElse(message, ""));
        return "user/ViewUsersList";
    }

    @GetMapping("/approve/{id}")
    public String approveUser(@PathVariable(name = "id") Long userId, RedirectAttributes redirectAttributes) {
        boolean response = userService.approveUser(userId);
        if (!response) {
            redirectAttributes.addFlashAttribute(Constants.MESSAGE_ATTRIBUTE, "Approval Unsuccessful");
        } else {
            redirectAttributes.addFlashAttribute(Constants.MESSAGE_ATTRIBUTE, "Approval Successful");
        }
        return "redirect:/user/list";
    }

    @GetMapping("/reject/{id}")
    public String rejectUser(@PathVariable(name = "id") Long userId, RedirectAttributes redirectAttributes) {
        boolean response = userService.rejectUser(userId);
        if (!response) {
            redirectAttributes.addFlashAttribute(Constants.MESSAGE_ATTRIBUTE, "Rejection Unsuccessful");
        } else {
            redirectAttributes.addFlashAttribute(Constants.MESSAGE_ATTRIBUTE, "Rejection Successful");
        }
        return "redirect:/user/list";
    }

}
