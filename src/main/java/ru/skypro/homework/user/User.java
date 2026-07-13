package ru.skypro.homework.user;

import lombok.Data;

@Data
public class User {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
}
