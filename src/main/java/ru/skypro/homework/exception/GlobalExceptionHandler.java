package ru.skypro.homework.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Глобальный обработчик исключений для веб-слоя приложения.
 * <p>
 * Перехватывает ошибки, возникающие при обработке запросов контроллерами,
 * и возвращает стандартизированный ответ в формате {@link AnswerErrorDto}.
 * Предотвращает утечку системных трейсов во внешнюю среду.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает случай, когда параметр пути/запроса имеет некорректный тип
     * (например, передана строка вместо числа).
     * <p>
     * Формирует понятное сообщение в зависимости от ожидаемого типа: UUID, число или иной тип.
     * </p>
     *
     * @param ex      исключение с информацией о неверном типе
     * @param request HTTP-запрос
     * @return 400 с описанием ошибки
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Параметр пути имеет некорректный тип данных (например, передана строка вместо числа)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                             HttpServletRequest request) {
        Class<?> requiredType = ex.getRequiredType();
        String typeName = (requiredType != null) ? requiredType.getSimpleName() : "";

        String message = switch (typeName) {
            case "UUID" ->
                    String.format(ExceptionConstants.TYPE_MISMATCH_UUID, ex.getName());
            case "Long", "Integer" ->
                    String.format(ExceptionConstants.TYPE_MISMATCH_NUMBER, ex.getName());
            default ->
                    String.format(ExceptionConstants.TYPE_MISMATCH_DEFAULT, ex.getName());
        };

        AnswerErrorDto error =  AnswerErrorDto.of(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        logError(HttpStatus.BAD_REQUEST, request, error);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    /**
     * Обрабатывает некорректный JSON в теле запроса.
     *
     * @param ex      исключение десериализации
     * @param request HTTP-запрос
     * @return 400 с сообщением {@link ExceptionConstants#MALFORMED_JSON_REQUEST}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Тело запроса содержит некорректный JSON или невозможно десериализовать в DTO",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleReadableException(HttpMessageNotReadableException ex,
                                                                  HttpServletRequest request) {
        AnswerErrorDto error =  AnswerErrorDto.of(
                HttpStatus.BAD_REQUEST.name(),
                ExceptionConstants.MALFORMED_JSON_REQUEST
        );
        String detailedCause = ex.getMessage();
        logError(HttpStatus.NOT_FOUND, request, error, detailedCause);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    /**
     * Обрабатывает использование недопустимого HTTP-метода для данного эндпоинта.
     *
     * @param ex      исключение с информацией о недопустимом методе
     * @param request HTTP-запрос
     * @return 405 с сообщением {@link ExceptionConstants#METHOD_NOT_ALLOWED}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ApiResponse(
            responseCode = "405",
            description = "Использован недопустимый HTTP-метод для данного эндпоинта",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                   HttpServletRequest request) {
        AnswerErrorDto error =  AnswerErrorDto.of(
                HttpStatus.METHOD_NOT_ALLOWED.name(),
                ExceptionConstants.METHOD_NOT_ALLOWED
        );

        String detailedCause = ex.getMessage();
        logError(HttpStatus.BAD_REQUEST, request, error, detailedCause);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    /**
     * Обрабатывает отсутствие обязательного параметра запроса.
     *
     * @param ex      исключение с информацией о пропущенном параметре
     * @param request HTTP-запрос
     * @return 400 с сообщением {@link ExceptionConstants#MISSING_PARAMETER}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Отсутствует обязательный параметр запроса",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleMissingParam(MissingServletRequestParameterException ex,
                                                             HttpServletRequest request) {
        AnswerErrorDto error =  AnswerErrorDto.of(
                HttpStatus.BAD_REQUEST.name(),
                ExceptionConstants.MISSING_PARAMETER
        );
        String detailedCause = ex.getMessage();
        logError(HttpStatus.NOT_FOUND, request, error, detailedCause);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Обрабатывает запрос к несуществующему URL-ресурсу.
     *
     * @param ex      исключение с информацией о не найденном ресурсе
     * @param request HTTP-запрос
     * @return 404 с сообщением {@link ExceptionConstants#RESOURCE_NOT_FOUND}
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ApiResponse(
            responseCode = "404",
            description = "Запрашиваемый ресурс (URL) не найден",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleNoResourceFound(NoResourceFoundException ex,
                                                                HttpServletRequest request) {
        AnswerErrorDto error =  AnswerErrorDto.of(
                HttpStatus.NOT_FOUND.name(),
                ExceptionConstants.RESOURCE_NOT_FOUND
        );

        String detailedCause = ex.getMessage();
        logError(HttpStatus.NOT_FOUND, request, error, detailedCause);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Обрабатывает попытку регистрации с уже занятым email.
     *
     * @param e       исключение {@link UserAlreadyExistsException}
     * @param request HTTP-запрос
     * @return 409 с сообщением {@link ExceptionConstants#USER_ALREADY_EXIST}
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ApiResponse(
            responseCode = "409",
            description = "Пользователь уже существует",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleUserAlreadyExists(UserAlreadyExistsException e,
                                                                  HttpServletRequest request) {
        AnswerErrorDto error = AnswerErrorDto.of(
                HttpStatus.CONFLICT.name(),
                e.getMessage()
        );
        logError(HttpStatus.CONFLICT, request, error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Обрабатывает отсутствие сущности в базе данных (объявление, пользователь и т.д.).
     *
     * @param e       исключение {@link EntityNotFoundException}
     * @param request HTTP-запрос
     * @return 404 с сообщением из исключения
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ApiResponse(
            responseCode = "404",
            description = "Сущность не найдена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleEntityNotFound(EntityNotFoundException e,
                                                               HttpServletRequest request) {
        AnswerErrorDto error = AnswerErrorDto.of(
                HttpStatus.NOT_FOUND.name(),
                e.getMessage()
        );
        logError(HttpStatus.NOT_FOUND, request, error);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Обрабатывает ошибки валидации входных DTO ({@code @Valid}).
     *
     * @param ex      исключение с деталями валидации
     * @param request HTTP-запрос
     * @return 400 с описанием ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Ошибки валидации входных данных (нарушены ограничения @Valid)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        AnswerErrorDto error = AnswerErrorDto.of(
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage()
        );
        logError(HttpStatus.BAD_REQUEST, request, error);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Обрабатывает неверные учётные данные при аутентификации.
     *
     * @param ex      исключение {@link BadCredentialsException}
     * @param request HTTP-запрос
     * @return 401 с описанием ошибки
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ApiResponse(
            responseCode = "401",
            description = "Неверный логин или пароль",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        AnswerErrorDto error = AnswerErrorDto.of(
                HttpStatus.UNAUTHORIZED.name(),
                ex.getMessage()
        );
        logError(HttpStatus.UNAUTHORIZED, request, error);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Обрабатывает попытку смены пароля с неверным текущим паролем.
     *
     * @param ex      исключение {@link InvalidOldPasswordException}
     * @param request HTTP-запрос
     * @return 400 с сообщением {@link ExceptionConstants#INVALID_OLD_PASSWORD}
     */
    @ExceptionHandler(InvalidOldPasswordException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Текущий пароль указан неверно",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleInvalidOldPassword(
            InvalidOldPasswordException ex, HttpServletRequest request) {

        AnswerErrorDto error = AnswerErrorDto.of(
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage()
        );
        logError(HttpStatus.BAD_REQUEST, request, error);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Обрабатывает попытку доступа к ресурсу без необходимых прав.
     *
     * @param ex      исключение {@link AccessDeniedException}
     * @param request HTTP-запрос
     * @return 403 с описанием ошибки доступа
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponse(
            responseCode = "403",
            description = "Нет прав на доступ к данному ресурсу",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        AnswerErrorDto error = AnswerErrorDto.of(
                HttpStatus.FORBIDDEN.name(),
                ex.getMessage()
        );
        logError(HttpStatus.FORBIDDEN, request, error);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Обрабатывает внутреннюю ошибку при создании пользователя (не удалось получить ID).
     *
     * @param ex      исключение {@link UserCreationException}
     * @param request HTTP-запрос
     * @return 500 с сообщением {@link ExceptionConstants#USER_CREATED_NOT_FOUND}
     */
    @ExceptionHandler(UserCreationException.class)
    @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера при создании пользователя",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AnswerErrorDto.class))
    )
    public ResponseEntity<AnswerErrorDto> handleUserCreationException(UserCreationException ex,
                                                                      HttpServletRequest request) {
        AnswerErrorDto error = AnswerErrorDto.of(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ex.getMessage()
        );
        logError(HttpStatus.INTERNAL_SERVER_ERROR, request, error);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Логирует информацию об ошибке: статус, HTTP-метод и URL запроса.
     *
     * @param status HTTP-статус
     * @param request HTTP-запрос
     * @param error  DTO с описанием ошибки
     */
    private void logError(HttpStatus status, HttpServletRequest request, AnswerErrorDto error) {
        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                status.value(),
                status.name(),
                method,
                url,
                error);
    }
    private void logError(HttpStatus status, HttpServletRequest request, AnswerErrorDto error, String techDetails) {
        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {} | Tech details: {}",
                status.value(),
                status.name(),
                method,
                url,
                error,
                techDetails);
    }
}
