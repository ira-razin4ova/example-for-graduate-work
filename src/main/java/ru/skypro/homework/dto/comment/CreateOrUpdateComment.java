package ru.skypro.homework.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания или обновления комментария.
 * <p>
 * Содержит текст комментария с валидацией длины от 8 до 64 символов.
 * </p>
 */
@Schema(description = "Данные для создания или обновления комментария")
public record CreateOrUpdateComment(
        @Schema(description = "Текст комментария", example = "Хороший товар")
        @NotBlank(message = "Комментарий не может быть пустым")
        @Size(min = 8, max = 64)
        String text
) {
}
