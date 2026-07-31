package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAd;
import ru.skypro.homework.dto.ad.ExtendedAd;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.util.SecurityUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private AdMapper adMapper;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AdService adService;

    private static final Integer AD_ID = 1;
    private static final String TEST_EMAIL = "user@example.com";
    private static final Integer USER_ID_PK = 2;

    private User author;
    private Ad testAd;
    private CreateOrUpdateAd createOrUpdateAdDto;
    private AdDto adDto;
    private ExtendedAd extendedAdDto;

    @BeforeEach
    void setUp() {

        author = new User();
        author.setId(1);
        author.setEmail(TEST_EMAIL);

        testAd = new Ad();
        testAd.setId(AD_ID);
        testAd.setTitle("Продам гараж");
        testAd.setAuthor(author);

        createOrUpdateAdDto = new CreateOrUpdateAd("Продам гараж", 100000, "Отличный гараж");

        adDto = AdDto.builder()
                .author(USER_ID_PK)
                .image("photo/1.png")
                .pk(AD_ID)
                .price(100000)
                .title("Продам гараж")
                .build();

        extendedAdDto = ExtendedAd.builder()
                .pk(AD_ID)
                .title("Продам гараж")
                .price(100000)
                .authorFirstName("Василий")
                .authorLastName("Петров")
                .email(TEST_EMAIL)
                .description("Отличный гараж")
                .phone("+7 999 999 99-99")
                .image("photo/1.png")
                .build();


        lenient().when(userDetails.getUsername()).thenReturn(TEST_EMAIL);
    }

    @Nested
    @DisplayName("Тестирование метода getListAd()")
    class GetListAdTests {

        @Test
        @DisplayName("Успешное получение списка всех объявлений")
        void getListAd_Success_ReturnsAdsDto() {

            when(adRepository.findAll()).thenReturn(List.of(testAd));
            when(adMapper.toDto(testAd)).thenReturn(adDto);


            AdsDto result = adService.getListAd();

            assertNotNull(result);
            assertEquals(1, result.count(), "Количество объявлений должно быть равно 1");
            assertEquals(1, result.results().size());
            assertEquals(adDto, result.results().get(0));

            verify(adRepository, times(1)).findAll();
            verify(adMapper, times(1)).toDto(testAd);
        }
    }

    @Nested
    @DisplayName("Тестирование метода createAd()")
    class CreateAdTests {

        @Test
        @DisplayName("Успешное создание нового объявления")
        void createAd_Success_ReturnsAdDto() throws java.io.IOException {
            when(adMapper.toEntity(createOrUpdateAdDto)).thenReturn(testAd);
            when(userService.checkUser(TEST_EMAIL)).thenReturn(author);
            when(adRepository.save(testAd)).thenReturn(testAd);
            when(adMapper.toDto(testAd)).thenReturn(adDto);

            AdDto result = adService.createAd(createOrUpdateAdDto, userDetails, null);

            assertNotNull(result);
            assertEquals(adDto, result);

            verify(userService, times(1)).checkUser(TEST_EMAIL);
            verify(adRepository, times(1)).save(testAd);
            verify(adMapper, times(1)).toDto(testAd);
        }
    }

    @Nested
    @DisplayName("Тестирование метода getAdBuId()")
    class GetAdByIdTests {

        @Test
        @DisplayName("Успешное получение расширенной информации об объявлении по ID")
        void getAdBuId_Success_ReturnsExtendedAd() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.of(testAd));
            when(adMapper.toExtendedDto(testAd)).thenReturn(extendedAdDto);

            ExtendedAd result = adService.getAdBuId(AD_ID);

            assertNotNull(result);
            assertEquals(extendedAdDto, result);
            verify(adRepository, times(1)).findById(AD_ID);
        }

        @Test
        @DisplayName("Ошибка получения, если объявление не найдено в БД")
        void getAdBuId_NotFound_ThrowsEntityNotFoundException() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> adService.getAdBuId(AD_ID)
            );

            assertEquals("Неправильный идентификатор объявления", exception.getMessage());
            verifyNoInteractions(adMapper);
        }

    }
    @Nested
    @DisplayName("Тестирование метода deleteAd()")
    class DeleteAdTests {

        @Test
        @DisplayName("Успешное удаление объявления при наличии прав")
        void deleteAd_Success() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.of(testAd));

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
                //Перехватываем статический метод SecurityUtils
                //Ничего не делаем в doNothing(), статик отработает без ошибок (права есть)

                adService.deleteAd(AD_ID, userDetails);


                verify(adRepository, times(1)).delete(testAd);
                securityUtilsMock.verify(
                        () -> SecurityUtils.checkModifyPermission(author, userDetails),
                        times(1)
                );
            }
        }

        @Test
        @DisplayName("Ошибка удаления, если объявление не найдено")
        void deleteAd_NotFound_ThrowsEntityNotFoundException() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> adService.deleteAd(AD_ID, userDetails)
            );

            verify(adRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Тестирование метода updateAd()")
    class UpdateAdTests {

        @Test
        @DisplayName("Успешное обновление объявления при наличии прав")
        void updateAd_Success_ReturnsUpdatedAdDto() {
            when(adRepository.findById(AD_ID)).thenReturn(Optional.of(testAd));
            when(adMapper.toDto(testAd)).thenReturn(adDto);

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
                
                AdDto result = adService.updateAd(AD_ID, createOrUpdateAdDto, userDetails);
                
                assertNotNull(result);
                assertEquals(adDto, result);

                verify(adMapper, times(1)).updateAd(createOrUpdateAdDto, testAd);
                verify(adMapper, times(1)).toDto(testAd);
                securityUtilsMock.verify(
                        () -> SecurityUtils.checkModifyPermission(author, userDetails),
                        times(1)
                );
            }
        }

        @Test
        @DisplayName("Ошибка обновления, если объявление не найдено")
        void updateAd_NotFound_ThrowsEntityNotFoundException() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> adService.updateAd(AD_ID, createOrUpdateAdDto, userDetails)
            );

            verify(adMapper, never()).updateAd(any(), any());
        }
    }

    @Nested
    @DisplayName("Тестирование метода getListAdUserAuth()")
    class GetListAdUserAuthTests {

        @Test
        @DisplayName("Успешное получение объявлений авторизованного пользователя")
        void getListAdUserAuth_Success_ReturnsUserAdsDto() {

            when(adRepository.findAllByAuthorEmail(TEST_EMAIL)).thenReturn(List.of(testAd));
            when(adMapper.toDto(testAd)).thenReturn(adDto);

            AdsDto result = adService.getListAdUserAuth(userDetails);

            assertNotNull(result);
            assertEquals(1, result.count());
            assertEquals(adDto, result.results().get(0));

            verify(adRepository, times(1)).findAllByAuthorEmail(TEST_EMAIL);
            verify(adMapper, times(1)).toDto(testAd);
        }
    }
}
