package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.exception.ExceptionConstants;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.util.SecurityUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

/**
 * Сервис для работы с комментариями.
 * <p>
 * Содержит бизнес-логику по управлению комментариями к объявлениям:
 * получение, создание, обновление и удаление.
 * </p>
 * <p>
 * Список комментариев кэшируется в Redis (кэш {@code comments}, ключ — ID объявления).
 * При добавлении, обновлении или удалении комментария кэш автоматически очищается.
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
     * <p>
     * Результат кэшируется в Redis (кэш {@code comments}, ключ — ID объявления) на 10 минут.
     * Кэш очищается при добавлении, обновлении или удалении комментария к данному объявлению.
     * </p>
     *
     * @param idAd ID объявления
     * @return {@link CommentsDto} со списком комментариев
     */
    @Cacheable(value = "comments", key = "#idAd")
    public CommentsDto getListComments(Integer idAd) {
        checkAdBuId(idAd);
        List<Comment> commentList = commentRepository.findAllByAd_Id(idAd);
        List<CommentDto> commentsDto = commentList.stream()
                .map(commentMapper::toDto)
                .toList();

        return CommentsDto.of(commentsDto);
    }

    /**
     * Создаёт новый комментарий к объявлению от имени авторизованного пользователя.
     * <p>
     * Кэш {@code comments} для данного объявления очищается автоматически.
     * </p>
     *
     * @param idAd        ID объявления
     * @param dto         данные комментария
     * @param userDetails данные авторизованного пользователя
     * @return {@link CommentDto} созданного комментария
     */
    @CacheEvict (value = "comments", key = "#idAd")
    @Transactional
    public CommentDto createComment(Integer idAd, CreateOrUpdateComment dto, UserDetails userDetails) {
        Ad ad = adRepository.findById(idAd)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.AD_NOT_FOUND));

        Comment comment = commentMapper.toEntity(dto);
        comment.setAd(ad);
        comment.setAuthor(userService.checkUser(userDetails.getUsername()));
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    /**
     * Обновляет комментарий (только автор или администратор).
     * <p>
     * Кэш {@code comments} для данного объявления очищается автоматически.
     * </p>
     *
     * @param idAd        ID объявления
     * @param commentId   ID комментария
     * @param dto         новые данные комментария
     * @param userDetails данные авторизованного пользователя
     * @return {@link CommentDto} с обновлёнными данными
     */
    @CacheEvict (value = "comments", key = "#idAd")
    @Transactional
    public CommentDto updateComment(Integer idAd, Integer commentId, CreateOrUpdateComment dto, UserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.COMMENT_NOT_FOUND));
        checkAdBuId(idAd);
        SecurityUtils.checkModifyPermission(comment.getAuthor(), userDetails);
        comment.setText(dto.text());
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    /**
     * Удаляет комментарий (только автор или администратор).
     * <p>
     * Кэш {@code comments} для данного объявления очищается автоматически.
     * </p>
     *
     * @param idAd        ID объявления
     * @param idComment   ID комментария
     * @param userDetails данные авторизованного пользователя
     */
    @CacheEvict (value = "comments", key = "#idAd")
    @Transactional
    public void deleteComment(Integer idAd, Integer idComment, UserDetails userDetails) {
        Comment comment = commentRepository.findById(idComment).
                orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.COMMENT_NOT_FOUND));
        if (!comment.getAd().getId().equals(idAd)) {
            throw new EntityNotFoundException(ExceptionConstants.AD_NOT_FOUND);
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
        adRepository.findById(idAd).orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.AD_NOT_FOUND));
    }
}
