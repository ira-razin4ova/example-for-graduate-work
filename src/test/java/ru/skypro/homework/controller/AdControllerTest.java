package ru.skypro.homework.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAd;
import ru.skypro.homework.dto.ad.ExtendedAd;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.user.Role;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AdController adController;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Ad testAd;
    private static final String RAW_PASSWORD = "password123";

    @BeforeEach
    void setUp() {

        commentRepository.deleteAll();
        adRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setPassword(passwordEncoder.encode(RAW_PASSWORD));
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");
        testUser.setPhone("+79991234567");
        testUser.setRole(Role.USER);
        testUser = userRepository.save(testUser);

        testAd = new Ad();
        testAd.setTitle("Продам гараж");
        testAd.setPrice(100000);
        testAd.setDescription("Отличный гараж");
        testAd.setAuthor(testUser);
        testAd = adRepository.save(testAd);
    }

    @Test
    void contextLoaded() {
        assertThat(adController).isNotNull();
    }

    @Test
    @DisplayName("Успешное получение списка объявлений (GET /ads)")
    void getListAd_Success_ReturnsAdsDto() {

        String url = "http://localhost:" + port + "/ads";

        ResponseEntity<AdsDto> response = testRestTemplate.getForEntity(url, AdsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Успешное создание объявления (POST /ads)")
    void createAd_Success_ReturnsAdDto() {
        String url = "http://localhost:" + port + "/ads";

        CreateOrUpdateAd createOrUpdateAd = new CreateOrUpdateAd("Продам авто", 500000, "Хорошая машина");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dtoHeaders = new HttpHeaders();
        dtoHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateOrUpdateAd> dtoEntity = new HttpEntity<>(createOrUpdateAd, dtoHeaders);
        body.add("properties", dtoEntity);

        ByteArrayResource imageResource = new ByteArrayResource("test-image-content".getBytes()) {
            @Override
            public String getFilename() {
                return "test.png";
            }
        };
        HttpHeaders imageHeaders = new HttpHeaders();
        imageHeaders.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<ByteArrayResource> imageEntity = new HttpEntity<>(imageResource, imageHeaders);
        body.add("image", imageEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<AdDto> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .postForEntity(url, requestEntity, AdDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Продам авто", response.getBody().title());
    }

    @Test
    @DisplayName("Успешное получение объявления по ID (GET /ads/{id})")
    void getAd_Success_ReturnsExtendedAd() {

            String url = "http://localhost:" + port + "/ads/" + testAd.getId();

            ResponseEntity<ExtendedAd> response = testRestTemplate.getForEntity(url, ExtendedAd.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Продам гараж", response.getBody().title());
            assertEquals(100000, response.getBody().price());

    }

    @Test
    @DisplayName("Успешное удаление объявления по ID (DELETE /ads/{id})")
    void removeAd_Success_ReturnsNoContent() {

        String url = "http://localhost:" + port + "/ads/" + testAd.getId();

        ResponseEntity<Void> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);


        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertTrue(adRepository.findById(testAd.getId()).isEmpty(), "Объявление должно быть удалено из БД");
    }

    @Test
    @DisplayName("Получить объявления авторизованного пользователя (GET /ads/me)")
    void getAdsMe_Success_ReturnsAdsDto() {

        String url = "http://localhost:" + port + "/ads/me";

        ResponseEntity<AdsDto> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .getForEntity(url, AdsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }

    @Test
    @DisplayName("Успешное обновление объявления (PATCH /ads/{id})")
    void updateAd_Success_ReturnsAdDto() {
        String url = "http://localhost:" + port + "/ads/" + testAd.getId();

        CreateOrUpdateAd updateAdDto = new CreateOrUpdateAd("Обновленный авто", 550000, "Новое описание объявления");

        HttpEntity<CreateOrUpdateAd> requestEntity = new HttpEntity<>(updateAdDto);

        ResponseEntity<AdDto> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .exchange(url, HttpMethod.PATCH, requestEntity, AdDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Обновленный авто", response.getBody().title());
        assertEquals(550000, response.getBody().price());
    }
}

