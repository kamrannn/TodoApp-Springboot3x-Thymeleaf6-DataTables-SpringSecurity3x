package com.app.todo;

import com.app.todo.dto.RegistrationRequest;
import com.app.todo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void registerUser_Success() throws Exception {
        Mockito.when(userService.register(any(RegistrationRequest.class)))
                .thenReturn("User registered successfully");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", true))
                .andExpect(model().attribute("registerDTO", new RegistrationRequest()))
                .andExpect(view().name("auth/register"));
    }

    @Test
    void registerUser_EmailAlreadyExists() throws Exception {
        Mockito.when(userService.register(any(RegistrationRequest.class)))
                .thenReturn("Email already exists");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("emailAlreadyExists", true))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "email"))
                .andExpect(view().name("auth/register"));
    }

    @Test
    void registerUser_OtherError() throws Exception {
        Mockito.when(userService.register(any(RegistrationRequest.class)))
                .thenReturn("Some other error");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("errorMessage", "Some other error"))
                .andExpect(view().name("auth/register"));
    }
}

