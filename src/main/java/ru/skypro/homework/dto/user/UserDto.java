package ru.skypro.homework.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.skypro.homework.user.Role;

@Builder
@Schema(description = "Информация о пользователе")
public record UserDto(
        @Schema(description = "ID пользователя", example = "1")
        Integer id,

        @Schema(description = "Логин (email) пользователя", example = "user@mail.ru")
        String email,

        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Петров")
        String lastName,

        @Schema(description = "Телефон пользователя", example = "+7 900 000-00-00")
        String phone,

        @Schema(description = "Роль пользователя", example = "USER")
        Role role,

        @Schema(description = "Ссылка на аватар пользователя")
        String image
) {
}
