package ru.skypro.homework.exception;

/**
 * Исключение, выбрасываемое при попытке регистрации пользователя с уже существующим email.
 * <p>
 * Возникает в {@link ru.skypro.homework.service.impl.AuthServiceImpl#register},
 * если пользователь с таким именем уже зарегистрирован в системе.
 * </p>
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
