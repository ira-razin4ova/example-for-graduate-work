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
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.AuthService;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Авторизация", description = "Регистрация/авторизация")
public class AuthController {

    private final AuthService authService;

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
    public ResponseEntity<Login> register(
            @RequestBody @Valid Register register) {
        Login userId = authService.register(register);

            return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
