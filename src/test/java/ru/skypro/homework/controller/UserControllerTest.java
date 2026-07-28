package ru.skypro.homework.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import ru.skypro.homework.dto.user.NewPasswordRequestDto;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.model.user.Role;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AdRepository adRepository;

    private User testUser;
    private static final String RAW_PASSWORD = "password123";

    @Autowired
    private CommentController commentController;


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
    }

    @Test
    void contextLoaded() {
        assertThat(userController).isNotNull();
    }

    @Test
    @DisplayName("Успешное обновление информации о пользователе (PATCH /users/me)")
    void updateUser_Success_ReturnsUserDto() {

        String url = "http://localhost:" + port + "/users/me";

        UpdateUser updateComment = new UpdateUser("Петр", "Петров", "+7 000 000-00-00");

        HttpEntity<UpdateUser> requestEntity = new HttpEntity<>(updateComment);

        ResponseEntity<UserDto> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .exchange(url, HttpMethod.PATCH, requestEntity, UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Петров", response.getBody().lastName());
    }

    @Test
    @DisplayName("Успешное обновление пароля (POST /users/set_password)")
    void updatePassword_Success() {

        String url = "http://localhost:" + port + "/users/set_password";

        NewPasswordRequestDto requestDto = new NewPasswordRequestDto(RAW_PASSWORD, "newPassword123");

        HttpEntity<NewPasswordRequestDto> requestEntity = new HttpEntity<>(requestDto);

        ResponseEntity<Void> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .exchange(url, HttpMethod.POST, requestEntity, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Успешное получение информации о текущем пользователе (GET /users/me)")
    void getUser_Success_ReturnsUserDto() {
        String url = "http://localhost:" + port + "/users/me";


        ResponseEntity<UserDto> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .getForEntity(url, UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getEmail(), response.getBody().email());
    }

    @Test
    @DisplayName("Успешное обновление аватара пользователя (200 OK)")
    void updateUserImage_Success() {
        String url = "http://localhost:" + port + "/users/me/image";

        byte[] fileContent = "fake-image-data".getBytes();
        ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return "avatar.jpg";
            }
        };

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .exchange(url, HttpMethod.PATCH, requestEntity, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
}
