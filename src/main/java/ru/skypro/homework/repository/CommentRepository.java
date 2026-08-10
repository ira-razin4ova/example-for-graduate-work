package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * <p>
     * Использует JPQL-запрос с {@code JOIN FETCH}, чтобы предзагрузить автора каждого
     * комментария одним запросом и избежать проблемы N+1.
     * </p>
     *
     * @param adId ID объявления
     * @return список комментариев с предзагруженными авторами
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.ad.id = :adId")
    List<Comment> findAllByAd_Id(@Param("adId") Integer adId);
}