package ru.skypro.homework.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Список комментариев")
public record CommentsDto(
        @Schema(description = "Общее количество комментариев", example = "10")
        Integer count,

        @Schema(description = "Список комментариев")
        List<CommentDto> results
) {
}

