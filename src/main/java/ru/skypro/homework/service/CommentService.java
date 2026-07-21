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
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.util.SecurityUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdRepository adRepository;

    private final UserService userService;

    public CommentsDto getListComments(Integer idAd) {
        checkAdBuId(idAd);
        List<Comment> commentList = commentRepository.findAllByAd_Id(idAd);
        List<CommentDto> commentsDto = commentList.stream()
                .map(commentMapper::toDto)
                .toList();

        return new CommentsDto(commentList.size(), commentsDto);
    }

    @Transactional
    public CommentDto createComment(Integer idAd, CreateOrUpdateComment dto, UserDetails userDetails) {
        checkAdBuId(idAd);

        Comment comment = commentMapper.toEntity(dto);
        comment.setId(idAd);
        comment.setAuthor(userService.checkUser(userDetails.getUsername()));
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto updateComment(Integer idAd, Integer commentId, CreateOrUpdateComment dto, UserDetails userDetails) {
        commentRepository.findById(commentId).
                orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария"));
        checkAdBuId(idAd);

        Comment comment = commentMapper.toEntity(dto);
        SecurityUtils.checkModifyPermission(comment.getAuthor(), userDetails);
        comment.setText(dto.text());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

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

    private void checkAdBuId(Integer idAd) {
        adRepository.findById(idAd).orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"));
    }
}
