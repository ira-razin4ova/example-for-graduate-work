package ru.skypro.homework.dto.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * DTO с информацией об объявлении для отображения в списке.
 * <p>
 * Содержит ID автора, ссылку на изображение, ID объявления, цену и заголовок.
 * </p>
 */
@Builder
@Schema(description = "Объявление")
public record AdDto(
        @Schema(description = "ID автора объявления", example = "1")
        Integer author,

        @Schema(description = "Ссылка на картинку объявления", example = "photo/1.png")
        String image,

        @Schema(description = "ID объявления", example = "1")
        Integer pk,

        @Schema(description = "Цена объявления", example = "1000")
        Integer price,

        @Schema(description = "Заголовок объявления", example = "Объявление")
        String title
) {
}
