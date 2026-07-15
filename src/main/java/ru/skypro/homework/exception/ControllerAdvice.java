package ru.skypro.homework.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик исключений для веб-слоя приложения Starbank.
 * <p>
 * Этот класс перехватывает ошибки, возникающие на этапе обработки запросов контроллерами,
 * изолирует их от клиента и возвращает стандартизированный ответ в формате {@link AnswerErrorDto}.
 * Предотвращает утечку системных трейсов во внешнюю среду.
 * </p>
 *
 * @author Твоё Irina Razinkova
 * @version 1.0
 */

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Параметр пути имеет некорректный тип данных (например, передана строка вместо числа)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String typeName = (requiredType != null) ? requiredType.getSimpleName() : "";

        String message = switch (typeName) {
            case "UUID" -> String.format("Параметр '%s' должен быть валидным UUID (например, cd515076-5d8a...)", ex.getName());
            case "Long", "Integer" -> String.format("Параметр '%s' должен быть целым числом", ex.getName());
            default -> String.format("Параметр '%s' имеет некорректный тип данных", ex.getName());
        };

        AnswerErrorDto error = new AnswerErrorDto(
                HttpStatus.BAD_REQUEST.name(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Тело запроса содержит некорректный JSON или невозможно десериализовать в DTO",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleReadableException(HttpMessageNotReadableException ex) {
        AnswerErrorDto error = new AnswerErrorDto(
                HttpStatus.BAD_REQUEST.name(),
                "Malformed JSON Request",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ApiResponse(
            responseCode = "405",
            description = "Использован недопустимый HTTP-метод для данного эндпоинта",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        AnswerErrorDto error = new AnswerErrorDto(
                HttpStatus.METHOD_NOT_ALLOWED.name(),
                "Method Not Allowed",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Отсутствует обязательный параметр запроса",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleMissingParam(MissingServletRequestParameterException ex) {
        AnswerErrorDto error = new AnswerErrorDto(
                HttpStatus.BAD_REQUEST.name(),
                "Missing Parameter",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ApiResponse(
            responseCode = "404",
            description = "Запрашиваемый ресурс (URL) не найден",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleNoResourceFound(NoResourceFoundException ex) {
        AnswerErrorDto error = new AnswerErrorDto(
                HttpStatus.NOT_FOUND.name(),
                "Resource Not Found",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ApiResponse(
            responseCode = "409",
            description = "Пользователь уже существует",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleUserAlreadyExists(UserAlreadyExistsException e) {
        AnswerErrorDto error = new AnswerErrorDto(
                "USER_ALREADY_EXISTS",           // code
                e.getMessage(),                  // message
                LocalDateTime.now()              // timestamp
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ApiResponse(
            responseCode = "404",
            description = "Сущность не найдена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleEntityNotFound(EntityNotFoundException e) {
        AnswerErrorDto error = new AnswerErrorDto(
                "ENTITY_NOT_FOUND",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AnswerErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {


        AnswerErrorDto error = new AnswerErrorDto(
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserCreationException.class)
    @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера при создании пользователя",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleUserCreationException(UserCreationException ex) {
        AnswerErrorDto error = new AnswerErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
