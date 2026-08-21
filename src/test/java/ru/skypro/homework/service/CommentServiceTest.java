package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Nested;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.util.SecurityUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CommentService commentService;

    private static final Integer AD_ID = 1;
    private static final Integer COMMENT_ID = 10;
    private static final String TEST_EMAIL = "user@example.com";

    private User author;
    private Ad testAd;
    private Comment testComment;
    private CreateOrUpdateComment createOrUpdateCommentDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {

        author = new User();
        author.setEmail(TEST_EMAIL);

        testAd = new Ad();
        testAd.setId(AD_ID);
        testAd.setAuthor(author);

        testComment = new Comment();
        testComment.setId(COMMENT_ID);
        testComment.setText("Отличный гараж!");
        testComment.setAuthor(author);
        testComment.setAd(testAd);

        createOrUpdateCommentDto = new CreateOrUpdateComment("Отличный гараж!");

        commentDto = CommentDto.builder()
                .pk(COMMENT_ID)
                .text("Отличный гараж!")
                .author(1)
                .build();

        lenient().when(userDetails.getUsername()).thenReturn(TEST_EMAIL);
    }

    @Nested
    @DisplayName("Тестирование метода getListComments()")
    class GetListCommentsTests {

        @Test
        @DisplayName("Успешное получение списка комментариев к объявлению")
        void getListComments_Success_ReturnsCommentsDto() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.of(testAd));
            when(commentRepository.findAllByAd_Id(AD_ID)).thenReturn(List.of(testComment));
            when(commentMapper.toDto(testComment)).thenReturn(commentDto);

            CommentsDto result = commentService.getListComments(AD_ID);

            assertNotNull(result);
            assertEquals(1, result.count());
            assertEquals(1, result.results().size());
            assertEquals(commentDto, result.results().get(0));

            verify(adRepository, times(1)).findById(AD_ID);
            verify(commentRepository, times(1)).findAllByAd_Id(AD_ID);
            verify(commentMapper, times(1)).toDto(testComment);
        }

        @Test
        @DisplayName("Ошибка, если объявление не найдено при запросе списка комментариев")
        void getListComments_AdNotFound_ThrowsEntityNotFoundException() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> commentService.getListComments(AD_ID)
            );

            assertEquals("Объявление не найдено", exception.getMessage());
            verifyNoInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Тестирование метода createComment()")
    class CreateCommentTests {

        @Test
        @DisplayName("Успешное создание нового комментария")
        void createComment_Success_ReturnsCommentDto() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.of(testAd));
            when(commentMapper.toEntity(createOrUpdateCommentDto)).thenReturn(testComment);
            when(userService.checkUser(TEST_EMAIL)).thenReturn(author);
            when(commentRepository.save(testComment)).thenReturn(testComment);
            when(commentMapper.toDto(testComment)).thenReturn(commentDto);

            CommentDto result = commentService.createComment(AD_ID, createOrUpdateCommentDto, userDetails);

            assertNotNull(result);
            assertEquals(commentDto, result);

            verify(adRepository, times(1)).findById(AD_ID);
            verify(userService, times(1)).checkUser(TEST_EMAIL);
            verify(commentRepository, times(1)).save(testComment);
            verify(commentMapper, times(1)).toDto(testComment);
        }

        @Test
        @DisplayName("Ошибка создания, если объявление не существует в БД")
        void createComment_AdNotFound_ThrowsEntityNotFoundException() {

            when(adRepository.findById(AD_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> commentService.createComment(AD_ID, createOrUpdateCommentDto, userDetails)
            );

            assertEquals("Объявление не найдено", exception.getMessage());
            verify(commentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Тестирование метода updateComment()")
    class UpdateCommentTests {

        @Test
        @DisplayName("Успешное обновление комментария при наличии прав")
        void updateComment_Success_ReturnsUpdatedCommentDto() {

            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(testComment));
            when(adRepository.findById(AD_ID)).thenReturn(Optional.of(testAd));
            when(commentRepository.save(testComment)).thenReturn(testComment);
            when(commentMapper.toDto(testComment)).thenReturn(commentDto);

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {


                CommentDto result = commentService.updateComment(AD_ID, COMMENT_ID, createOrUpdateCommentDto, userDetails);


                assertNotNull(result);
                assertEquals(commentDto, result);

                verify(commentRepository, times(1)).findById(COMMENT_ID);
                verify(adRepository, times(1)).findById(AD_ID);
                verify(commentRepository, times(1)).save(testComment);

                securityUtilsMock.verify(
                        () -> SecurityUtils.checkModifyPermission(author, userDetails),
                        times(1)
                );
            }
        }

        @Test
        @DisplayName("Ошибка обновления, если комментарий не найден по ID")
        void updateComment_CommentNotFound_ThrowsEntityNotFoundException() {

            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> commentService.updateComment(AD_ID, COMMENT_ID, createOrUpdateCommentDto, userDetails)
            );

            assertEquals("Комментарий не найден", exception.getMessage());
            verify(adRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("Тестирование метода deleteComment()")
    class DeleteCommentTests {

        @Test
        @DisplayName("Успешное удаление комментария при наличии прав")
        void deleteComment_Success() {
            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(testComment));

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {

                commentService.deleteComment(AD_ID, COMMENT_ID, userDetails);

                verify(commentRepository, times(1)).delete(testComment);
                securityUtilsMock.verify(
                        () -> SecurityUtils.checkModifyPermission(author, userDetails),
                        times(1)
                );
            }
        }

        @Test
        @DisplayName("Ошибка удаления, если комментарий не найден в БД")
        void deleteComment_CommentNotFound_ThrowsEntityNotFoundException() {

            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> commentService.deleteComment(AD_ID, COMMENT_ID, userDetails)
            );

            assertEquals("Комментарий не найден", exception.getMessage());
            verify(commentRepository, never()).delete(any());
        }
    }
}
