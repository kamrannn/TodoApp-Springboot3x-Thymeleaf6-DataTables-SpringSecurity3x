package com.app.todo.service;

import com.app.todo.dto.RegistrationRequest;
import com.app.todo.enums.Role;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import com.app.todo.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    public final BCryptPasswordEncoder encoder;
    private final Utils utils;
    private final UserRepository userRepository;

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";

    private final static String USER_DISABLED_MSG =
            "user with email %s is disabled in the database.";

    public String register(RegistrationRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return "Email already exists";
        } else {
            User user = new User(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    encoder.encode(request.getPassword()),
                    Role.USER
            );
            user.setEnabled(true);
            userRepository.save(user);
            return "User registered successfully";
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> appUser = userRepository.findByEmail(email);
        if (appUser.isPresent() && !appUser.get().isEnabled()) {
            throw new UsernameNotFoundException(String.format(USER_DISABLED_MSG, email));
        } else if (appUser.isPresent()) {
            return appUser.get();
        } else {
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email));
        }
    }
}
