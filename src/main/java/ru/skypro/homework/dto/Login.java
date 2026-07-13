package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Авторизация")
public record Login

        (@Schema(description = "username", example = "username@mail.ru")
         @NotNull(message = "Имя пользователя не может быть пустым")
         String username,

         @Schema(description = "password", example = "123abc")
         @NotNull(message = "Пароль не может быть пустым")
         String password) {
}
