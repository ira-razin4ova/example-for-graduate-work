package ru.skypro.homework.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ при успешной регистрации")
public record ResponseAnswerRegisterDto(
        @Schema(description = "ID созданного пользователя", example = "1")
        Integer id
) {
}
