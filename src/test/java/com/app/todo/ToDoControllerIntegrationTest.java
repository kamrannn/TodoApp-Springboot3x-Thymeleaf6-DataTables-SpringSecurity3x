package com.app.todo;

import com.app.todo.model.ToDo;
import com.app.todo.model.User;
import com.app.todo.service.ToDoService;
import com.app.todo.service.UserService;
import com.app.todo.util.Constants;
import com.app.todo.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ToDoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoService toDoService;

    @MockBean
    private UserService userService;

    @MockBean
    private Utils utils;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@example.com");

        Mockito.when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(utils.getLoggedInUser()).thenReturn(user);
        Mockito.when(toDoService.getAllToDoItems()).thenReturn(Collections.emptyList());
        Mockito.when(toDoService.findToDosByTitleContaining("test")).thenReturn(Collections.emptyList());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void viewAllToDoItems() throws Exception {
        mockMvc.perform(get("/viewToDoList"))
                .andExpect(status().isOk())
                .andExpect(view().name("ViewToDoList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attribute("list", Collections.emptyList()));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void searchToDos() throws Exception {
        mockMvc.perform(get("/searchToDo")
                        .param("title", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("ViewToDoList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attribute("list", Collections.emptyList()));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void addToDoItem() throws Exception {
        mockMvc.perform(get("/addToDoItem"))
                .andExpect(status().isOk())
                .andExpect(view().name("AddToDoItem"))
                .andExpect(model().attributeExists("todo"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void saveToDoItem() throws Exception {
        mockMvc.perform(post("/saveToDoItem")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Test ToDo")
                        .param("date", "2024-12-31") // ensure future date
                        .param("status", "Incomplete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addToDoItem"))
                .andExpect(flash().attribute(Constants.MESSAGE_ATTRIBUTE, "Save Failed"));
    }


    @Test
    @WithMockUser(username = "user@example.com")
    void editToDoItem() throws Exception {
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Test ToDo");
        todo.setDate(new Date());
        todo.setStatus("Incomplete");

        Mockito.when(toDoService.getToDoItemById(1L)).thenReturn(todo);

        mockMvc.perform(get("/editToDoItem/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("EditToDoItem"))
                .andExpect(model().attributeExists("todo"))
                .andExpect(model().attribute("todo", todo));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void editSaveToDoItem() throws Exception {
        mockMvc.perform(post("/editSaveToDoItem")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("title", "Updated ToDo")
                        .param("date", "2024-12-31") // ensure future date
                        .param("status", "Incomplete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/editToDoItem/" + 1))
                .andExpect(flash().attribute(Constants.MESSAGE_ATTRIBUTE, "Edit Failed"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void deleteToDoItem() throws Exception {
        Mockito.when(toDoService.deleteToDoItem(1L)).thenReturn(true);

        mockMvc.perform(get("/deleteToDoItem/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/viewToDoList"))
                .andExpect(flash().attribute(Constants.MESSAGE_ATTRIBUTE, "Delete Successful"));
    }
}

