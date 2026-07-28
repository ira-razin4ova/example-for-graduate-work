package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.dto.auth.ResponseAnswerRegisterDto;
import ru.skypro.homework.exception.UserAlreadyExistsException;
import ru.skypro.homework.exception.UserCreationException;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encoded_password123";
    private static final String WRONG_PASSWORD = "wrongPassword";
    private static final String UNKNOW_EMAIL = "notfound@example.com";

    private User existingUser;
    private Register registerDto;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1);
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setPassword(ENCODED_PASSWORD);

        registerDto = new Register(
                TEST_EMAIL,
                RAW_PASSWORD,
                "Иван",
                "Иванов",
                "+7 999 000-00-00",
                "USER"
        );
    }

    @Nested
    @DisplayName("Тестирование метода login()")
    class LoginTests {

        @Test
        @DisplayName("Успешный вход, если пользователь существует и пароль совпадает")
        void login_Success_ReturnsTrue() {
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
            when(encoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

            boolean result = authService.login(TEST_EMAIL, RAW_PASSWORD);

            assertTrue(result, "Логин должен быть успешным (true)");
            verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
            verify(encoder, times(1)).matches(RAW_PASSWORD, ENCODED_PASSWORD);
        }
    }

    @Test
    @DisplayName("Отказ во входе, если введён неверный пароль")
    void login_WrongPassword_ReturnsFalse() {


        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(encoder.matches(WRONG_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        boolean result = authService.login(TEST_EMAIL, WRONG_PASSWORD);

        assertFalse(result, "Логин должен вернуть false при неверном пароле");
    }

    @Test
    @DisplayName("Отказ во входе, если пользователь не найден в БД")
    void login_UserNotFound_ReturnsFalse() {

        when(userRepository.findByEmail(UNKNOW_EMAIL)).thenReturn(Optional.empty());

        boolean result = authService.login(UNKNOW_EMAIL, RAW_PASSWORD);

        assertFalse(result, "Логин должен вернуть false, если пользователя нет");
        verifyNoInteractions(encoder);
    }

    @Nested
    @DisplayName("Тестирование метода register()")
    class RegisterTests {

        @Test
        @DisplayName("Успешная регистрация нового пользователя")
        void register_Success_ReturnsDtoWithUserId() {

            when(userRepository.existsByEmail(registerDto.username())).thenReturn(false);
            when(encoder.encode(registerDto.password())).thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            when(userRepository.findByEmail(registerDto.username())).thenReturn(Optional.of(existingUser));

            ResponseAnswerRegisterDto result = authService.register(registerDto);

            assertNotNull(result);
            assertEquals(1, result.id(), "Возвращённый ID пользователя должен совпадать");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Ошибка регистрации, если пользователь с таким email уже существует")
        void register_UserAlreadyExists_ThrowsException() {

            when(userRepository.existsByEmail(registerDto.username())).thenReturn(true);

            UserAlreadyExistsException exception = assertThrows(
                    UserAlreadyExistsException.class,
                    () -> authService.register(registerDto)
            );

            assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
            verifyNoInteractions(encoder);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Ошибка регистрации, если после сохранения не удалось достать юзера из БД")
        void register_UserNotFoundAfterSave_ThrowsUserCreationException() {

            when(userRepository.existsByEmail(registerDto.username())).thenReturn(false);
            when(encoder.encode(registerDto.password())).thenReturn(ENCODED_PASSWORD);
            when(userRepository.findByEmail(registerDto.username())).thenReturn(Optional.empty());

            UserCreationException exception = assertThrows(
                    UserCreationException.class,
                    () -> authService.register(registerDto)
            );

            assertEquals("Не удалось получить ID созданного пользователя", exception.getMessage());
        }
    }
}
