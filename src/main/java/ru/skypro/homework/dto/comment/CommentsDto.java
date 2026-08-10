package ru.skypro.homework.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO со списком комментариев и общим количеством.
 */
@Schema(description = "Список комментариев")
public record CommentsDto(
        @Schema(description = "Общее количество комментариев", example = "10")
        Integer count,

        @Schema(description = "Список комментариев")
        List<CommentDto> results
) {
    /**
     * Создаёт DTO из списка комментариев.
     * <p>
     * Если список равен {@code null}, он заменяется пустым списком,
     * чтобы избежать {@code NullPointerException}.
     * </p>
     *
     * @param results список комментариев (может быть {@code null})
     * @return {@link CommentsDto} с количеством и списком комментариев
     */
    public static CommentsDto of (List <CommentDto> results) {
        List<CommentDto> saveResult = (results != null) ? results : List.of();
        return new CommentsDto(saveResult.size(), saveResult);
    }
}

