package ru.skypro.homework.exception;

/**
 * Исключение, выбрасываемое при вводе неверного текущего пароля.
 * <p>
 * Возникает в {@link ru.skypro.homework.service.UserService#changePassword},
 * когда пользователь пытается сменить пароль, но указал неверный старый пароль.
 * </p>
 */
public class InvalidOldPasswordException extends RuntimeException {
    public InvalidOldPasswordException(String message) {
        super(message);
    }
}
