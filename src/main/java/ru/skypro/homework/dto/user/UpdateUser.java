package ru.skypro.homework.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * DTO для обновления информации о пользователе.
 * <p>
 * Содержит имя, фамилию и телефон с валидацией форматов.
 * Телефон должен соответствовать формату +7 XXX XXX-XX-XX.
 * </p>
 */
@Builder
@Schema(description = "Данные для обновления информации о пользователе")
public record UpdateUser(
        @Schema(description = "Имя пользователя", example = "Иван")
        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 3, max = 10, message = "Имя должно быть от 3 до 10 символов")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Петров")
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(min = 3, max = 10, message = "Фамилия должна быть от 3 до 10 символов")
        String lastName,

        @Schema(description = "Телефон пользователя", example = "+7 900 000-00-00")
        @NotBlank(message = "Телефон не может быть пустым")
        @Pattern(regexp = "^\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$",
                message = "Телефон должен быть в формате +7 XXX XXX-XX-XX")
        String phone
) {
}
