package ru.skypro.homework.dto.announcements;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Расширенная информация об объявлении")
public record ExtendedAd(
        @Schema(description = "ID объявления", example = "1")
        Integer pk,

        @Schema(description = "Имя автора", example = "Василий")
        String authorFirstName,

        @Schema(description = "Фамилия автора", example = "Петров")
        String authorLastName,

        @Schema(description = "Описание", example = "Продается книга, в хорошем состоянии")
        String description,

        @Schema(description = "Email автора", example = "email@mail.com")
        String email,

        @Schema(description = "Ссылка на картинку", example = "photo/1.png")
        String image,

        @Schema(description = "Телефон автора", example = "79999999999")
        String phone,

        @Schema(description = "Цена", example = "1200")
        Integer price,

        @Schema(description = "Заголовок",example = "Объявление")
        String title
) {}
