package ru.skypro.homework.dto.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для создания или обновления объявления")
public record CreateOrUpdateAd(
        @Schema(description = "Заголовок объявления", example = "Продается")
        @NotBlank(message = "Заголовок не может быть пустым")
        @Size(min = 4, max = 32)
        String title,

        @Schema(description = "Цена объявления", example = "1200")
        @NotBlank(message = "Цена не может быть пустой")
        @Min(0) @Max(10000000)
        Integer price,

        @Schema(description = "Описание объявления", example = "Продается книга")
        @Size(min = 8, max = 64)
        String description
) {}
