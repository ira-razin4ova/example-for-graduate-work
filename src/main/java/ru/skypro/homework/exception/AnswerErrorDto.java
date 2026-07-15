package ru.skypro.homework.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Ответ с информацией об ошибке")
public record AnswerErrorDto(
        @Schema(description = "Код ошибки", example = "USER_ALREADY_EXISTS")
        String code,

        @Schema(description = "Сообщение об ошибке", example = "Пользователь с таким именем уже существует")
        String message,

        @Schema(description = "Время возникновения ошибки", example = "2026-07-13T14:30:00", type = "string", format = "date-time")
        LocalDateTime timestamp
) {}
