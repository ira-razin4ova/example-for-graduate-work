package ru.skypro.homework.exception;

/**
 * Исключение, выбрасываемое при невозможности получить ID созданного пользователя.
 * <p>
 * Возникает в {@link ru.skypro.homework.service.impl.AuthServiceImpl#register},
 * если после сохранения пользователя не удалось найти его в базе данных.
 * </p>
 */
public class UserCreationException  extends RuntimeException {
  public UserCreationException  (String message) {
        super(message);
    }
}
