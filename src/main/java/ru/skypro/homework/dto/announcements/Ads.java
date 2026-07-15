package ru.skypro.homework.dto.announcements;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Список объявлений")
public record Ads(
        @Schema(description = "Общее количество объявлений", example = "12")
        Integer count,

        @Schema(description = "Список объявлений")
        List<Ad> results
) {
}
