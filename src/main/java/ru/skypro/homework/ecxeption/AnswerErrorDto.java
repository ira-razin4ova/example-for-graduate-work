package ru.skypro.homework.ecxeption;

import java.time.LocalDateTime;

public record AnswerErrorDto(
        String code,
        String message,
        LocalDateTime timestamp) {
}
