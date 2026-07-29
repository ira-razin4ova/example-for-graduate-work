package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.user.User;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link User}.
 * <p>
 * Предоставляет базовые CRUD-операции и дополнительные методы
 * для поиска и проверки существования пользователей по email.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Находит пользователя по email.
     *
     * @param username email пользователя
     * @return {@link Optional}, содержащий найденного пользователя, или пустой, если пользователь не найден
     */
    Optional<User> findByEmail(String username);

    /**
     * Проверяет, существует ли пользователь с указанным email.
     *
     * @param email email для проверки
     * @return {@code true}, если пользователь с таким email уже существует, иначе {@code false}
     */
    boolean existsByEmail(String email);
}
