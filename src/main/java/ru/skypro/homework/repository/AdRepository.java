package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ad;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link Ad}.
 * <p>
 * Предоставляет базовые CRUD-операции и методы для поиска объявлений по автору.
 * </p>
 */
@Repository
public interface AdRepository extends JpaRepository <Ad, Integer>{

    /**
     * Возвращает все объявления, созданные пользователем с указанным email.
     *
     * @param email email автора
     * @return список объявлений автора
     */
    List<Ad> findAllByAuthorEmail(String email);

    /**
     * Возвращает все объявления с предзагруженным автором.
     * <p>
     * Использует {@link EntityGraph}, чтобы загрузить связь {@code author} одним SQL-запросом
     * (JOIN), избегая проблемы N+1 при обращении к автору каждого объявления.
     * </p>
     *
     * @return список всех объявлений с авторами
     */
    @EntityGraph(attributePaths = {"author"})
    List<Ad> findAll();
}