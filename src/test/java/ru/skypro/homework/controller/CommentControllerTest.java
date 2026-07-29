package ru.skypro.homework.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.user.Role;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CommentController commentController;

    private User testUser;
    private Ad testAd;
    private static final String RAW_PASSWORD = "password123";
    private Comment testComment;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        testComment = new Comment();
        testComment.setAuthor(testUser);
        testComment.setText("Текст комментария");
        testComment.setAd(testAd);
        testComment.setCreatedAt(1111L);
        testComment = commentRepository.save(testComment);

    }

    @Test
    void contextLoaded() {
        assertThat(commentController).isNotNull();
    }

    @Test
    @DisplayName("Успешное получение списка комментариев к объявлению (GET /ads/{id}/comments)")
    void getComments_Success_ReturnsCommentsDto() {

        String url = "http://localhost:" + port + "/ads/" + testAd.getId() + "/comments";

        ResponseEntity<CommentsDto> response = testRestTemplate.getForEntity(url, CommentsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    @DisplayName("Успешное добавление комментария к объявлению (POST /ads/{id}/comments)")
    void addComment_Success_ReturnsCommentDto() {

        String url = "http://localhost:" + port + "/ads/" + testAd.getId() + "/comments";

        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment("Отличный гараж, покупаю!");

        ResponseEntity<CommentDto> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .postForEntity(url, createOrUpdateComment, CommentDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    @DisplayName("Успешное удаление комментария по ID (DELETE /{adId}/comments/{commentId})")
    void removeComment_Success_ReturnsNoContent() {

        String url = "http://localhost:" + port + "/ads/" + testAd.getId() + "/comments/" + testComment.getId();

        ResponseEntity<Void> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);


        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertTrue(commentRepository.findById(testComment.getId()).isEmpty(), "Комментарий должен быть удален из БД");
    }


    @Test
    @DisplayName("Успешное обновление комментария по ID (PATCH /ads/{adId}/comments/{commentId})")
    void updateComment_Success_ReturnsCommentDto() {

        String url = "http://localhost:" + port + "/ads/" + testAd.getId() + "/comments/" + testComment.getId();

        CreateOrUpdateComment updateComment = new CreateOrUpdateComment("Обновленный комментарий");

        HttpEntity<CreateOrUpdateComment> requestEntity = new HttpEntity<>(updateComment);

        ResponseEntity<CommentDto> response = testRestTemplate
                .withBasicAuth(testUser.getEmail(), RAW_PASSWORD)
                .exchange(url, HttpMethod.PATCH, requestEntity, CommentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Обновленный комментарий", response.getBody().text());
    }
}
