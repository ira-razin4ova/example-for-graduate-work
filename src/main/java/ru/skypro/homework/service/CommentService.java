package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.util.SecurityUtils;

import java.util.List;

/**
 * Сервис для работы с комментариями.
 * <p>
 * Содержит бизнес-логику по управлению комментариями к объявлениям:
 * получение, создание, обновление и удаление.
 * </p>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdRepository adRepository;

    private final UserService userService;

    /**
     * Возвращает список комментариев для указанного объявления.
     *
     * @param idAd ID объявления
     * @return {@link CommentsDto} со списком комментариев
     */
    public CommentsDto getListComments(Integer idAd) {
        checkAdBuId(idAd);
        List<Comment> commentList = commentRepository.findAllByAd_Id(idAd);
        List<CommentDto> commentsDto = commentList.stream()
                .map(commentMapper::toDto)
                .toList();

        return new CommentsDto(commentList.size(), commentsDto);
    }

    /**
     * Создаёт новый комментарий к объявлению от имени авторизованного пользователя.
     *
     * @param idAd        ID объявления
     * @param dto         данные комментария
     * @param userDetails данные авторизованного пользователя
     * @return {@link CommentDto} созданного комментария
     */
    @Transactional
    public CommentDto createComment(Integer idAd, CreateOrUpdateComment dto, UserDetails userDetails) {
        Ad ad = adRepository.findById(idAd)
                .orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"));

        Comment comment = commentMapper.toEntity(dto);
        comment.setAd(ad);
        comment.setAuthor(userService.checkUser(userDetails.getUsername()));
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    /**
     * Обновляет комментарий (только автор или администратор).
     *
     * @param idAd        ID объявления
     * @param commentId   ID комментария
     * @param dto         новые данные комментария
     * @param userDetails данные авторизованного пользователя
     * @return {@link CommentDto} с обновлёнными данными
     */
    @Transactional
    public CommentDto updateComment(Integer idAd, Integer commentId, CreateOrUpdateComment dto, UserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария"));
        checkAdBuId(idAd);
        SecurityUtils.checkModifyPermission(comment.getAuthor(), userDetails);
        comment.setText(dto.text());
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    /**
     * Удаляет комментарий (только автор или администратор).
     *
     * @param idAd        ID объявления
     * @param idComment   ID комментария
     * @param userDetails данные авторизованного пользователя
     */
    @Transactional
    public void deleteComment(Integer idAd, Integer idComment, UserDetails userDetails) {
        Comment comment = commentRepository.findById(idComment).
                orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария"));
        if (comment.getAd().equals(idAd)) {
            throw new EntityNotFoundException("Неправильный идентификатор объявления");
        }
        SecurityUtils.checkModifyPermission(comment.getAuthor(), userDetails);
        commentRepository.delete(comment);
    }

    /**
     * Проверяет существование объявления по ID.
     *
     * @param idAd ID объявления
     * @throws jakarta.persistence.EntityNotFoundException если объявление не найдено
     */
    private void checkAdBuId(Integer idAd) {
        adRepository.findById(idAd).orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"));
    }
}
