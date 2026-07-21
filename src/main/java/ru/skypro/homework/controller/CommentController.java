package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

import java.util.Collections;

@RestController
@RequestMapping("/ads")
@Tag(name = "Комментарии", description = "API для работы с комментариями")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Получить комментарии объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список комментариев"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @GetMapping("/ads/{id}/comments")
    public ResponseEntity<CommentsDto> getComments(
            @Parameter(description = "ID объявления", example = "1")
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(commentService.getListComments(id));
    }

    @Operation(summary = "Добавить комментарий")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Комментарий добавлен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @Parameter(description = "ID объявления", example = "1")
            @PathVariable Integer id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные комментария",
                    required = true
            )
            @RequestBody @Valid CreateOrUpdateComment comment,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(id, comment, userDetails));
    }

    @Operation(summary = "Удалить комментарий")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Комментарий удалён"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление (чужой комментарий)"),
            @ApiResponse(responseCode = "404", description = "Комментарий или объявление не найдено")
    })
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID объявления", example = "1")
            @PathVariable Integer adId,

            @Parameter(description = "ID комментария", example = "5")
            @PathVariable Integer commentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        commentService.deleteComment(adId, commentId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить комментарий")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий обновлён"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование (чужой комментарий)"),
            @ApiResponse(responseCode = "404", description = "Комментарий или объявление не найдено")
    })
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @Parameter(description = "ID объявления", example = "1")
            @PathVariable Integer adId,

            @Parameter(description = "ID комментария", example = "5")
            @PathVariable Integer commentId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные комментария",
                    required = true
            )
            @RequestBody @Valid CreateOrUpdateComment comment,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(commentService.updateComment(adId, commentId, comment, userDetails));
    }
}