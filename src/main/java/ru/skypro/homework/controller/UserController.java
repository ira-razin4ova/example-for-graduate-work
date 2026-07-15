package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.user.NewPasswordRequestDto;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.service.UserService;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "API для работы с пользователями")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Обновить информацию о пользователе",
            description = "Обновляет данные пользователя по его ID. Пароль будет закодирован перед сохранением."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateUser dto) {

        UserDto updatedUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable @Positive Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить пароль")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль обновлён"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено")
    })
    @PostMapping("/set_password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody NewPasswordRequestDto dto) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить информацию об авторизованном пользователе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о пользователе"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser() {
        return ResponseEntity.ok(UserDto.builder().build());
    }

    @Operation(summary = "Обновить аватар пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аватар обновлён"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserImage(@RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok().build();
    }
}
