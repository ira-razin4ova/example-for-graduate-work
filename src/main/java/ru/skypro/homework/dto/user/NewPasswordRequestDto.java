package ru.skypro.homework.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для смены пароля")
public record NewPasswordRequestDto(
        @Schema(description = "Текущий пароль", example = "123abcDE")
        @NotBlank(message = "Текущий пароль не может быть пустым")
        @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
        String currentPassword,

        @Schema(description = "Новый пароль", example = "newPass123")
        @NotBlank(message = "Новый пароль не может быть пустым")
        @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
        String newPassword
) {
}
