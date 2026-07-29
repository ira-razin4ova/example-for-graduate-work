package ru.skypro.homework.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.auth.Login;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.dto.auth.ResponseAnswerRegisterDto;
import ru.skypro.homework.model.user.Role;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String EXISTING_EMAIL = "existing_user@example.com";
    private static final String RAW_PASSWORD = "password123";

    @BeforeEach
    void setUp() {

        commentRepository.deleteAll();
        adRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail(EXISTING_EMAIL);
        user.setPassword(passwordEncoder.encode(RAW_PASSWORD));
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        user.setRole(Role.USER);
        userRepository.save(user);
    }

        @Test
        @DisplayName("Успешный вход (200 OK) при корректных учетных данных")
        void login_Success_Returns200OK() {
            String url = "http://localhost:" + port + "/login";

            Login loginDto = new Login(EXISTING_EMAIL, RAW_PASSWORD);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Login> request = new HttpEntity<>(loginDto, headers);

            ResponseEntity<Void> response = testRestTemplate.postForEntity(url, request, Void.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }


        @Test
        @DisplayName("Успешная регистрация нового пользователя (201 Created)")
        void register_Success_Returns201Created() {
            String url = "http://localhost:" + port + "/register";

            String newEmail = "new_user@example.com";
            Register registerDto = new Register(
                    newEmail,
                    "newPassword123",
                    "Петр",
                    "Петров",
                    "+7 999 111-22-33",
                    "USER"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Register> request = new HttpEntity<>(registerDto, headers);

            ResponseEntity<ResponseAnswerRegisterDto> response = testRestTemplate.postForEntity(
                    url,
                    request,
                    ResponseAnswerRegisterDto.class
            );

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());

            assertTrue(userRepository.findByEmail(newEmail).isPresent());
    }
}
