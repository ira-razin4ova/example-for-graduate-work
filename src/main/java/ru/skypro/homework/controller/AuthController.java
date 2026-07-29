package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.auth.Login;
import ru.skypro.homework.dto.auth.ResponseAnswerRegisterDto;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.service.AuthService;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * <p>
 * Предоставляет endpoints для входа в систему и создания новой учётной записи.
 * Аутентификация выполняется через Basic Auth.
 * </p>
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Регистрация/авторизация")
public class AuthController {

    private final AuthService authService;

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param login данные для входа (email и пароль)
     * @return 200 OK при успешной аутентификации, 401 при неверных данных
     */
    @PostMapping("/login")
    @Operation(
            summary = "Вход в систему",
            description = "Аутентифицирует пользователя по имени и паролю. " +
                    "При успешной валидации возвращает 200, иначе 401."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аутентификация успешна"),
            @ApiResponse(responseCode = "401", description = "Неверное имя пользователя или пароль")
    })
    public ResponseEntity<Void> login(@RequestBody @Valid Login login) {
        if (authService.login(login.username(), login.password())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param register данные для регистрации
     * @return 201 Created с ID созданного пользователя
     */
    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает новую учетную запись. " +
                    "При успешной регистрации возвращает 201, при ошибке валидации — 400."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или пользователь уже существует")
    })
    public ResponseEntity<ResponseAnswerRegisterDto> register(
            @RequestBody @Valid Register register) {
        ResponseAnswerRegisterDto userId = authService.register(register);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userId);
    }
}
