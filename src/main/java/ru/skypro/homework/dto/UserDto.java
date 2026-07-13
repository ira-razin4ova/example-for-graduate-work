package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.skypro.homework.user.Role;

@Builder
@Schema(description = "Информация о пользователе")
public record UserDto(
        @Schema(description = "ID пользователя", example = "1")
        Long id,

        @Schema(description = "Имя пользователя", example = "user@mail.ru")
        String username,

        @Schema(description = "Имя", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия", example = "Петров")
        String lastName,

        @Schema(description = "Телефон", example = "79000000000")
        String phone,

        @Schema(description = "Роль пользователя", example = "USER")
        Role role
) {}
