package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.util.SecurityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование сервиса изображений ImageService")
class ImageServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ImageService imageService;

    @TempDir
    Path tempDir;

    private static final Integer AD_ID = 1;
    private static final String FILE_NAME = "test.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final byte[] CONTENT = "test-image".getBytes();

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "imagesDir", tempDir.resolve("images").toString());
        ReflectionTestUtils.setField(imageService, "avatarsDir", tempDir.resolve("avatars").toString());

        mockFile = new MockMultipartFile("image", FILE_NAME, CONTENT_TYPE, CONTENT);
    }

    @Nested
    @DisplayName("Тестирование метода saveImage()")
    class SaveImageTests {

        @Test
        @DisplayName("Успешное сохранение изображения")
        void saveImage_Success() throws IOException {
            String path = imageService.saveImage(AD_ID, mockFile);

            assertNotNull(path);
            assertTrue(Files.exists(Path.of(path)));
        }

        @Test
        @DisplayName("Ошибка, если передан неверный формат файла")
        void saveImage_InvalidFormat_ThrowsValidationException() {
            MockMultipartFile badFile = new MockMultipartFile("image", "test.txt", "text/plain", "data".getBytes());

            assertThrows(ValidationException.class, () -> imageService.saveImage(AD_ID, badFile));
        }
    }

    @Nested
    @DisplayName("Тестирование метода updateImage()")
    class UpdateImageTests {

        @Test
        @DisplayName("Успешное обновление изображения объявления")
        void updateImage_Success() throws IOException {
            Ad ad = new Ad();
            ad.setId(AD_ID);
            ad.setAuthor(new User());

            when(adRepository.findById(AD_ID)).thenReturn(Optional.of(ad));

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
                byte[] result = imageService.updateImage(AD_ID, mockFile, userDetails);

                assertNotNull(result);
                assertArrayEquals(CONTENT, result);
                verify(adRepository).save(ad);
                securityUtilsMock.verify(() -> SecurityUtils.checkModifyPermission(any(), any()));
            }
        }

        @Test
        @DisplayName("Ошибка, если объявление не найдено")
        void updateImage_AdNotFound_ThrowsEntityNotFoundException() {
            when(adRepository.findById(AD_ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    imageService.updateImage(AD_ID, mockFile, userDetails));
        }
    }

    @Nested
    @DisplayName("Тестирование метода userPhotoUser()")
    class UserPhotoUserTests {

        @Test
        @DisplayName("Успешное сохранение аватара пользователя")
        void userPhotoUser_Success() throws IOException {
            String path = imageService.userPhotoUser(AD_ID, mockFile);

            assertNotNull(path);
            assertTrue(Files.exists(Path.of(path)));
        }
    }
}