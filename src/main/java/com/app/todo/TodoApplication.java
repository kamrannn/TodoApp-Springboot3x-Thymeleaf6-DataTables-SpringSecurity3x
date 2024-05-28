package com.app.todo;

import com.app.todo.enums.Role;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TodoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User user = new User("admin", "admin", "admin@gmail.com", encoder.encode("admin"), Role.ADMIN);
            user.setEnabled(true);
            userRepository.save(user);
        }

        if (userRepository.findByEmail("kamran@gmail.com").isEmpty()) {
            User user = new User("Kamran", "Abbasi", "kamran@gmail.com", encoder.encode("kamran"), Role.USER);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

}
