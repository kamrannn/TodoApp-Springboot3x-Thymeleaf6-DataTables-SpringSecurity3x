package com.app.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//INSERT INTO `todo_app`.`users` (`id`, `email`, `enabled`, `first_name`, `last_name`, `locked`, `password`, `role`) VALUES ('1', 'admin@gmail.com', true, 'admin', 'admin', false, '$2a$10$V594lhL3Pi2UJSYnUgQ4WexyTWvuqujP/xImrRXTyLksbxpMcABKq', 'ADMIN');
//INSERT INTO `todo_app`.`users` (`id`, `email`, `enabled`, `first_name`, `last_name`, `locked`, `password`, `role`) VALUES ('2', 'kamran@gmail.com', true, 'Kamran', 'Abbasi', false, '$2a$10$usKhqGGzuL2JJ.TLqTNi.eQ4mKO/VzXsFF/y5HUPGKf3Rwb580CMe', 'USER');
@SpringBootApplication
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}
