package ru.skypro.homework.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO для регистрации нового пользователя.
 * <p>
 * Содержит все необходимые данные: email, пароль, имя, фамилию, телефон и роль.
 * Пароль должен быть от 8 до 16 символов, телефон в формате +7 XXX XXX-XX-XX.
 * </p>
 */
@Schema(description = "Данные для регистрации пользователя")
public record Register(
        @Schema(description = "Логин пользователя", example = "username@mail.ru")
        @NotBlank(message = "Логин не может быть пустым")
        @Email(message = "Введите корректный email")
        @Size(min = 4, max = 32, message = "Логин должен быть от 4 до 32 символов")
        String username,

        @Schema(description = "Пароль пользователя", example = "123abcDE")
        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
        String password,

        @Schema(description = "Имя пользователя", example = "Иван")
        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 2, max = 16, message = "Имя должно быть от 2 до 16 символов")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Петров")
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(min = 2, max = 16, message = "Фамилия должна быть от 2 до 16 символов")
        String lastName,

        @Schema(description = "Телефон пользователя", example = "+7 900 000-00-00")
        @NotBlank(message = "Телефон не может быть пустым")
        @Pattern(regexp = "^\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$",
                message = "Телефон должен быть в формате +7 XXX XXX-XX-XX")
        String phone,

        @Schema(description = "Роль пользователя", example = "USER")
        @NotBlank(message = "Роль обязательна")
        String role
) {
}
