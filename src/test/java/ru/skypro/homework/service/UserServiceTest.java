package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.user.NewPasswordRequestDto;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.exception.InvalidOldPasswordException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserService userService;

    private static final Integer USER_ID = 1;
    private static final String TEST_EMAIL = "user@example.com";
    private static final String OTHER_EMAIL = "other@example.com";
    private static final String OLD_PASSWORD = "oldPassword123";
    private static final String ENCODED_OLD_PASSWORD = "encodedOldPassword123";
    private static final String NEW_PASSWORD = "newPassword456";
    private static final String ENCODED_NEW_PASSWORD = "encodedNewPassword456";

    private User testUser;
    private UserDto userDto;
    private UpdateUser updateUserDto;
    private NewPasswordRequestDto newPasswordRequestDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(ENCODED_OLD_PASSWORD);
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");

        userDto = UserDto.builder()
                .id(USER_ID)
                .email(TEST_EMAIL)
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        updateUserDto = new UpdateUser("Петр", "Петров", "+79991234567");

        newPasswordRequestDto = new NewPasswordRequestDto(OLD_PASSWORD, NEW_PASSWORD);

        lenient().when(userDetails.getUsername()).thenReturn(TEST_EMAIL);
    }

    @Nested
    @DisplayName("Тестирование метода checkUser()")
    class CheckUserTests {

        @Test
        @DisplayName("Успешный поиск пользователя по email")
        void checkUser_Success_ReturnsUser() {
 
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

            User result = userService.checkUser(TEST_EMAIL);

            assertNotNull(result);
            assertEquals(TEST_EMAIL, result.getEmail());
            assertEquals(USER_ID, result.getId());
            verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        }

        @Test
        @DisplayName("Ошибка, если пользователь не найден по email")
        void checkUser_NotFound_ThrowsEntityNotFoundException() {
 
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.checkUser(TEST_EMAIL)
            );

            assertEquals("user not found", exception.getMessage());
            verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        }
    }

    @Nested
    @DisplayName("Тестирование метода infoAuthUser()")
    class InfoAuthUserTests {

        @Test
        @DisplayName("Успешное получение информации об авторизованном пользователе")
        void infoAuthUser_Success_ReturnsUserDto() {

            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
            when(userMapper.toDto(testUser)).thenReturn(userDto);

            UserDto result = userService.infoAuthUser(userDetails);

            assertNotNull(result);
            assertEquals(userDto, result);
            verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
            verify(userMapper, times(1)).toDto(testUser);
        }

        @Nested
        @DisplayName("Тестирование метода updateUser()")
        class UpdateUserTests {

            @Test
            @DisplayName("Успешное обновление данных пользователя")
            void updateUser_Success_ReturnsUpdatedUserDto() {
     
                when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
                when(userMapper.toDto(testUser)).thenReturn(userDto);
                
                UserDto result = userService.updateUser(userDetails, updateUserDto);

                assertNotNull(result);
                assertEquals(userDto, result);

                verify(userMapper, times(1)).updateFromDto(updateUserDto, testUser);
                verify(userMapper, times(1)).toDto(testUser);
            }

            @Test
            @DisplayName("Ошибка доступа, если email сущности не совпадает с логином из UserDetails")
            void updateUser_EmailMismatch_ThrowsAccessDeniedException() {
     
                User otherUser = new User();
                otherUser.setEmail(OTHER_EMAIL);

                when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(otherUser));

                AccessDeniedException exception = assertThrows(
                        AccessDeniedException.class,
                        () -> userService.updateUser(userDetails, updateUserDto)
                );

                assertEquals("Вы не можете изменить чужие данные", exception.getMessage());
                verify(userMapper, never()).updateFromDto(any(), any());
            }
        }

        @Nested
        @DisplayName("Тестирование метода changePassword()")
        class ChangePasswordTests {

            @Test
            @DisplayName("Успешная смена пароля")
            void changePassword_Success() {
     
                when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
                when(passwordEncoder.matches(OLD_PASSWORD, ENCODED_OLD_PASSWORD)).thenReturn(true);
                when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);

                userService.changePassword(newPasswordRequestDto, userDetails);
                
                assertEquals(ENCODED_NEW_PASSWORD, testUser.getPassword());
                verify(passwordEncoder, times(1)).matches(OLD_PASSWORD, ENCODED_OLD_PASSWORD);
                verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
                verify(userRepository, times(1)).save(testUser);
            }

            @Test
            @DisplayName("Ошибка смены пароля, если введён неверный текущий пароль")
            void changePassword_InvalidOldPassword_ThrowsInvalidOldPasswordException() {

                when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
                when(passwordEncoder.matches(OLD_PASSWORD, ENCODED_OLD_PASSWORD)).thenReturn(false);

                InvalidOldPasswordException exception = assertThrows(
                        InvalidOldPasswordException.class,
                        () -> userService.changePassword(newPasswordRequestDto, userDetails)
                );

                assertEquals("Неверный текущий пароль", exception.getMessage());
                verify(passwordEncoder, never()).encode(any());
                verify(userRepository, never()).save(any());
            }

            @Test
            @DisplayName("Ошибка доступа при попытке сменить пароль для чужого аккаунта")
            void changePassword_EmailMismatch_ThrowsAccessDeniedException() {

                User otherUser = new User();
                otherUser.setEmail(OTHER_EMAIL);

                when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(otherUser));

                AccessDeniedException exception = assertThrows(
                        AccessDeniedException.class,
                        () -> userService.changePassword(newPasswordRequestDto, userDetails)
                );

                assertEquals("Вы не можете изменить чужие данные", exception.getMessage());
                verify(passwordEncoder, never()).matches(any(), any());
                verify(userRepository, never()).save(any());
            }
        }
    }
}

