package ru.skypro.homework.service;

import ru.skypro.homework.dto.auth.ResponseAnswerRegisterDto;
import ru.skypro.homework.dto.auth.Register;

/**
 * Сервис аутентификации и регистрации пользователей.
 * <p>
 * Определяет контракт для выполнения входа в систему и регистрации новых пользователей.
 * </p>
 */
public interface AuthService {
    /**
     * Выполняет аутентификацию пользователя по имени и паролю.
     *
     * @param userName имя пользователя (email)
     * @param password пароль
     * @return {@code true} если аутентификация успешна, иначе {@code false}
     */
    boolean login(String userName, String password);

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param register DTO с данными для регистрации
     * @return {@link ResponseAnswerRegisterDto} с ID созданного пользователя
     */
    ResponseAnswerRegisterDto register(Register register);
}
