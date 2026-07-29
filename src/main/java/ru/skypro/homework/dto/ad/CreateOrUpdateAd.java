package ru.skypro.homework.dto.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * DTO для создания или обновления объявления.
 * <p>
 * Содержит заголовок (от 4 до 32 символов), цену (от 0 до 10 000 000)
 * и описание (от 8 до 64 символов).
 * </p>
 */
@Builder
@Schema(description = "Данные для создания или обновления объявления")
public record CreateOrUpdateAd(
        @Schema(description = "Заголовок объявления", example = "Продается")
        @NotBlank(message = "Заголовок не может быть пустым")
        @Size(min = 4, max = 32)
        String title,

        @Schema(description = "Цена объявления", example = "1200")
        @NotNull(message = "Цена не может быть пустой")
        @Min(0) @Max(10000000)
        Integer price,

        @Schema(description = "Описание объявления", example = "Продается книга")
        @Size(min = 8, max = 64)
        String description
) {}
