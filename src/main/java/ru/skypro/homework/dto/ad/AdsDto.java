package ru.skypro.homework.dto.ad;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO со списком объявлений и общим количеством.
 */
@Schema(description = "Список объявлений")
public record AdsDto(
        @Schema(description = "Общее количество объявлений", example = "12")
        Integer count,

        @Schema(description = "Список объявлений")
        List<AdDto> results
) {
    /**
     * Создаёт DTO из списка объявлений.
     * <p>
     * Если список равен {@code null}, он заменяется пустым списком,
     * чтобы избежать {@code NullPointerException}.
     * </p>
     *
     * @param results список объявлений (может быть {@code null})
     * @return {@link AdsDto} с количеством и списком объявлений
     */
    public static AdsDto of(List<AdDto> results) {
        List<AdDto> saveResults = (results != null) ? results : List.of();
        return new AdsDto(saveResults.size(), saveResults);
    }
}
