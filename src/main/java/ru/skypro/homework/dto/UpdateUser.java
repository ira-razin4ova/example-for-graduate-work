package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.skypro.homework.user.Role;

public record UpdateUser
        (@Schema(description = "username", example = "username@mail.ru")
         @NotNull(message = "Имя пользователя не может быть пустым")
         String username,

         @Schema(description = "password", example = "123abc")
         @NotNull(message = "Пароль пользователя не может быть пустым")
         String password,

         @Schema(description = "firstName", example = "Иван")
         @NotNull(message = "Имя не может быть пустым")
         String firstName,

         @Schema(description = "lastName", example = "Петров")
         @NotNull(message = "Фамилия пользователя не может быть пустым")
         String lastName,

         @Schema(description = "phone", example = "79000000000")
         @NotNull(message = "Номер телефона обязателен")
         String phone,

         @Schema(description = "role", example = "USER")
         Role role){
}
