package ru.skypro.homework.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * DTO с информацией о комментарии для отображения.
 * <p>
 * Содержит ID автора, его аватар и имя, дату создания, ID комментария и текст.
 * </p>
 */
@Builder
@Schema(description = "Комментарий")
public record CommentDto(
        @Schema(description = "ID автора комментария", example = "1")
        Integer author,

        @Schema(description = "Ссылка на аватар автора", example = "photo/id1.jpeg")
        String authorImage,

        @Schema(description = "Имя автора", example = "Константин")
        String authorFirstName,

        @Schema(description = "Дата создания в миллисекундах", example = "2006-12-12, 13:57:56")
        Long createdAt,

        @Schema(description = "ID комментария", example = "1")
        Integer pk,

        @Schema(description = "Текст комментария", example = "Хороший товар")
        String text
) {
}
