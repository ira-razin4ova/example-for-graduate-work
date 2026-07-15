package ru.skypro.homework.dto.announcements;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Объявление")
public record Ad(
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
