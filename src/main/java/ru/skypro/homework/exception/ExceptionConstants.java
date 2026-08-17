package ru.skypro.homework.exception;

/**
 * Константы с текстовыми сообщениями об ошибках.
 * <p>
 * Централизованно хранит все сообщения, возвращаемые {@link GlobalExceptionHandler}
 * и выбрасываемые в сервисном слое. Обеспечивает единообразие текстов ошибок
 * и удобство их сопровождения в одном месте.
 * </p>
 */
public final class ExceptionConstants {
    private ExceptionConstants () {}

    /** Сообщение для параметра пути с некорректным типом UUID. */
    public static final String TYPE_MISMATCH_UUID = "Параметр '%s' должен быть валидным UUID (например, cd515076-5d8a...)";

    /** Сообщение для параметра пути с некорректным числовым типом. */
    public static final String TYPE_MISMATCH_NUMBER = "Параметр '%s' должен быть целым числом";

    /** Сообщение для параметра пути с неизвестным типом данных. */
    public static final String TYPE_MISMATCH_DEFAULT = "Параметр '%s' имеет некорректный тип данных";

    /** Ошибка десериализации тела запроса (битый JSON). */
    public static final String MALFORMED_JSON_REQUEST = "Malformed JSON Request";

    /** Использование недопустимого HTTP-метода для эндпоинта. */
    public static final String METHOD_NOT_ALLOWED = "Method Not Allowed";

    /** Отсутствует обязательный параметр запроса. */
    public static final String MISSING_PARAMETER = "Missing Parameter";

    /** Запрашиваемый URL-ресурс не найден. */
    public static final String RESOURCE_NOT_FOUND = "Resource Not Found";

    /** Пользователь с указанным email уже зарегистрирован. */
    public static final String USER_ALREADY_EXIST = "Пользователь уже существует";

    /** Не удалось получить ID только что созданного пользователя. */
    public static final String USER_CREATED_NOT_FOUND = "Не удалось получить ID созданного пользователя";

    /** Объявление с указанным ID не найдено. */
    public static final String AD_NOT_FOUND = "Объявление не найдено";

    /** Комментарий с указанным ID не найден. */
    public static final String COMMENT_NOT_FOUND = "Комментарий не найдено";

    /** Загруженный файл не является поддерживаемым форматом изображения. */
    public static final String INVALID_IMAGE_FORMAT = "Это не картинка! Грузи только jpeg, png или gif.";

    /** Пользователь с указанным email не найден. */
    public static final String USER_NOT_FOUND = "Пользователь не найден";

    /** Пользователь пытается изменить данные другого пользователя. */
    public static final String ACCESS_DENIED  = "Вы не можете изменить чужие данные";

    /** Указан неверный текущий пароль при смене пароля. */
    public static final String INVALID_OLD_PASSWORD = "Неверный текущий пароль";

    /** У пользователя нет прав на изменение данного ресурса (не владелец и не ADMIN). */
    public  static final  String CHECK_MODIFY_PERMISSION = "Нет прав на изменение данного ресурса";
}
