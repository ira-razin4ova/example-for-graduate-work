package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Comment;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link Comment}.
 * <p>
 * Предоставляет базовые CRUD-операции и методы для поиска комментариев по объявлению.
 * </p>
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * Возвращает все комментарии для указанного объявления.
     *
     * @param adId ID объявления
     * @return список комментариев
     */
    List<Comment> findAllByAd_Id(Integer adId);
}