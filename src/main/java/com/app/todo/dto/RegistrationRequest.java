package com.app.todo.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
