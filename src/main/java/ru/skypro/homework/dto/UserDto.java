package ru.skypro.homework.dto;

import ru.skypro.homework.user.Role;

public record UserDto( String username,
         String password,
         String firstName,
         String lastName,
         String phone,
         Role role) {
}
