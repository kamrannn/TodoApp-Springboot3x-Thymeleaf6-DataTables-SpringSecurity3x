package com.app.todo.service;

import com.app.todo.enums.Role;
import com.app.todo.model.ToDo;
import com.app.todo.model.User;
import com.app.todo.repository.ToDoRepository;
import com.app.todo.util.Utils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;

    private final Utils utils;

    public ToDoService(ToDoRepository toDoRepository, Utils utils) {
        this.toDoRepository = toDoRepository;
        this.utils = utils;
    }

    // Retrieve all to-do items from the database
    public List<ToDo> getAllToDoItems() {
        List<ToDo> toDoList;
        User loggedInUser = utils.getLoggedInUser();
        if (loggedInUser != null && loggedInUser.getRole().equals(Role.USER)) {
            toDoList = toDoRepository.findAllByUserOrderByCreatedAtDesc(loggedInUser);
        } else {
            toDoList = toDoRepository.findAllByOrderByCreatedAtDesc();
        }
        return toDoList;
    }

    public List<ToDo> findToDosByTitleContaining(String title) {
        return toDoRepository.findByTitleContainingIgnoreCase(title);
    }

    // Retrieve a specific to-do item by its ID
    public ToDo getToDoItemById(Long id) {
        // Find and return a to-do item by its ID
        Optional<ToDo> optionalToDo = toDoRepository.findById(id);
        return optionalToDo.orElse(null);
    }

    // Update the status of a to-do item to "Completed"
    public boolean updateStatus(Long id) {
        Optional<ToDo> optionalToDo = toDoRepository.findById(id);
        if (optionalToDo.isEmpty()) {
            return false;
        } else {
            optionalToDo.get().setStatus("Completed");
            toDoRepository.save(optionalToDo.get());
            return true;
        }
    }

    // Save or update a to-do item in the database
    public boolean saveOrUpdateToDoItem(ToDo todo) {
        User loggedInUser = utils.getLoggedInUser();
        if (loggedInUser != null && todo.getUser() == null) {
            todo.setUser(loggedInUser);
        } else {
            return false;
        }
        // Save or update the provided to-do item in the database
        ToDo updateObj = toDoRepository.save(todo);

        // Check if the saved or updated to-do item exists in the database
        if (getToDoItemById(updateObj.getId()) != null) {
            return true; // Successfully saved or updated
        }

        return false; // Failed to save or update
    }

    // Delete a to-do item from the database by its ID
    public boolean deleteToDoItem(Long id) {
        // Delete the to-do item with the specified ID from the database
        toDoRepository.deleteById(id);

        // Check if the to-do item with the specified ID no longer exists in the database
        if (getToDoItemById(id) == null) {
            return true; // Successfully deleted
        }

        return false; // Failed to delete
    }
}
