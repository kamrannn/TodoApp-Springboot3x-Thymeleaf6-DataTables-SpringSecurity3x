package com.app.todo.controller;


import com.app.todo.model.ToDo;
import com.app.todo.model.User;
import com.app.todo.service.ToDoService;
import com.app.todo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ToDoController {

    private final ToDoService service;

    private final UserService userService;

    public ToDoController(ToDoService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping({"/", "/viewToDoList"})
    public String viewAllToDoItems(Model model, @ModelAttribute("message") String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userService.findByEmail(username);
        if (user.isEmpty()) {
            return "Home";
        }

        model.addAttribute("list", service.getAllToDoItems());
        model.addAttribute("message", Objects.requireNonNullElse(message, ""));
        return "ViewToDoList";
    }


    @GetMapping("/updateToDoStatus/{id}")
    public String updateToDoStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (service.updateStatus(id)) {
            redirectAttributes.addFlashAttribute("message", "Updated Successfully");
            return "redirect:/viewToDoList";
        }
        redirectAttributes.addFlashAttribute("message", "Update Failed");
        return "redirect:/viewToDoList";
    }

    @GetMapping("/addToDoItem")
    public String addToDoItem(Model model) {
        model.addAttribute("todo", new ToDo());
        return "AddToDoItem";
    }

    @PostMapping("/saveToDoItem")
    public String saveToDoItem(@Valid @ModelAttribute(name = "todo") ToDo todo, BindingResult result, RedirectAttributes redirectAttributes, Model model) throws Exception {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date todayDate = simpleDateFormat.parse(simpleDateFormat.format(new Date()));

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Save Failed");
            return "redirect:/addToDoItem";
        } else if (todo.getDate().before(todayDate)) {
            model.addAttribute("todo", todo);
            result.rejectValue("date", "error.date", "Date must be in the future");
            return "AddToDoItem";
        }
        todo.setStatus("Incomplete");
        if (service.saveOrUpdateToDoItem(todo)) {
            redirectAttributes.addFlashAttribute("message", "Save Successful");
            return "redirect:/viewToDoList";
        }
        redirectAttributes.addFlashAttribute("message", "Save Failed");
        return "redirect:/addToDoItem";
    }

    @GetMapping("/editToDoItem/{id}")
    public String editToDoItem(@PathVariable Long id, Model model) {
        model.addAttribute("todo", service.getToDoItemById(id));
        return "EditToDoItem";
    }

    @PostMapping("/editSaveToDoItem")
    public String editSaveToDoItem(@Valid @ModelAttribute(name = "todo") ToDo todo, BindingResult result, RedirectAttributes redirectAttributes, Model model) throws Exception {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date todayDate = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Edit Failed");
            return "redirect:/editToDoItem/" + todo.getId();
        } else if (todo.getDate().before(todayDate)) {
            model.addAttribute("todo", todo);
            result.rejectValue("date", "error.date", "Date must be in the future");
            return "EditToDoItem";
        }
        ToDo existingTodo = service.getToDoItemById(todo.getId());
        if (existingTodo != null) {
            todo.setStatus(existingTodo.getStatus());
        } else {
            todo.setStatus("Incomplete");
        }
        if (service.saveOrUpdateToDoItem(todo)) {
            redirectAttributes.addFlashAttribute("message", "Edit Successful");
            return "redirect:/viewToDoList";
        }

        redirectAttributes.addFlashAttribute("message", "Edit Failed");
        return "redirect:/editToDoItem/" + todo.getId();
    }

    @GetMapping("/deleteToDoItem/{id}")
    public String deleteToDoItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (service.deleteToDoItem(id)) {
            redirectAttributes.addFlashAttribute("message", "Delete Successful");
        } else {
            redirectAttributes.addFlashAttribute("message", "Delete Failed");
        }
        return "redirect:/viewToDoList";
    }


    @GetMapping("/searchToDo")
    public String searchToDos(@RequestParam String title, Model model) {
        model.addAttribute("list", service.findToDosByTitleContaining(title));
        model.addAttribute("message", "");
        return "ViewToDoList";
    }

}

