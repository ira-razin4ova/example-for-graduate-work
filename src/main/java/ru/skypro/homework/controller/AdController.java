package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAd;
import ru.skypro.homework.dto.ad.ExtendedAd;
import ru.skypro.homework.service.AdService;

import java.util.Collections;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @Operation(summary = "Получить все объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список объявлений")
    })
    @GetMapping
    public ResponseEntity<AdsDto> getAllAds() {
        return ResponseEntity.ok(adService.getListAd());
    }

    @Operation(summary = "Добавить объявление")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Объявление создано"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDto> addAd(
            @RequestPart("properties") @Valid CreateOrUpdateAd properties,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("image") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adService.createAd(properties,userDetails));
    }

    @Operation(summary = "Получить объявление по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объявление найдено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(
            @Parameter(description = "ID объявления") @PathVariable Integer id) {
        return ResponseEntity.ok(adService.getAdBuId(id));
    }

    @Operation(summary = "Удалить объявление")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Удалено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(
            @Parameter(description = "ID объявления") @PathVariable Integer id, UserDetails userDetails) {
        adService.deleteAd(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить объявление")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обновлено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AdDto> updateAds(
            @Parameter(description = "ID объявления") @PathVariable Integer id,
            @RequestBody @Valid CreateOrUpdateAd ad, @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(adService.updateAd(id, ad, userDetails));
    }

    @Operation(summary = "Получить объявления авторизованного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список объявлений"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @GetMapping("/me")
    public ResponseEntity<AdsDto> getAdsMe(@AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(adService.getListAdUserAuth(userDetails));
    }

    @Operation(summary = "Обновить картинку объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Картинка обновлена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateImage(
            @Parameter(description = "ID объявления") @PathVariable Integer id,
            @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok().build();
    }
}
