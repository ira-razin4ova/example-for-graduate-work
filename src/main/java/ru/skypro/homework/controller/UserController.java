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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.config.UserDetailsServiceImpl;
import ru.skypro.homework.dto.user.NewPasswordRequestDto;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.service.UserService;

/**
 * Контроллер для управления пользователями.
 * <p>
 * Предоставляет endpoints для получения и обновления информации об авторизованном пользователе,
 * смены пароля и загрузки аватара.
 * </p>
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "API для работы с пользователями")
public class UserController {

    private final UserService userService;

    /**
     * Обновляет информацию об авторизованном пользователе.
     *
     * @param dto         новые данные пользователя
     * @param userDetails данные авторизованного пользователя
     * @return {@link UserDto} с обновлённой информацией
     */
    @Operation(
            summary = "Обновить информацию о пользователе",
            description = "Обновляет данные авторизованного пользователя. Пароль будет закодирован перед сохранением."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Новые данные пользователя")
            @Valid @RequestBody UpdateUser dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.updateUser(userDetails, dto));
    }

    /**
     * Меняет пароль авторизованного пользователя.
     *
     * @param dto         запрос с текущим и новым паролем
     * @param userDetails данные авторизованного пользователя
     */
    @Operation(summary = "Обновить пароль")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Неверный текущий пароль")
    })
    @PostMapping("/set_password")
    public ResponseEntity<Void> updatePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Старый и новый пароль")
            @Valid @RequestBody NewPasswordRequestDto dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.changePassword(dto, userDetails);
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает информацию об авторизованном пользователе.
     *
     * @param userDetails данные авторизованного пользователя
     * @return {@link UserDto} с информацией о пользователе
     */
    @Operation(summary = "Получить информацию об авторизованном пользователе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о пользователе"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.infoAuthUser(userDetails));
    }

    /**
     * Обновляет аватар авторизованного пользователя.
     *
     * @param image       файл изображения (JPEG/PNG)
     * @param userDetails данные авторизованного пользователя
     */
    @Operation(summary = "Обновить аватар пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аватар обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректный файл изображения"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserImage(
            @Parameter(description = "Файл изображения (JPEG/PNG)", required = true)
            @RequestPart("image") MultipartFile image,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok().build();
    }
}
