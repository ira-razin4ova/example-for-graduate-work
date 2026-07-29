package ru.skypro.homework.service;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.user.NewPasswordRequestDto;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.exception.InvalidOldPasswordException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.UserRepository;


/**
 * Сервис для работы с пользователями.
 * <p>
 * Содержит бизнес-логику по управлению пользователями: получение информации,
 * обновление данных и смена пароля.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Находит пользователя по email или выбрасывает исключение.
     *
     * @param userName email пользователя
     * @return найденный пользователь
     * @throws jakarta.persistence.EntityNotFoundException если пользователь не найден
     */
    public User checkUser(String userName) {
        return userRepository.findByEmail(userName).orElseThrow(() -> new EntityNotFoundException("user not found"));
    }

    /**
     * Обновляет данные авторизованного пользователя.
     *
     * @param userDetails данные авторизованного пользователя
     * @param updateUser  новые данные для обновления
     * @return {@link UserDto} с обновлёнными данными
     * @throws org.springframework.security.access.AccessDeniedException если email из запроса не совпадает с авторизованным
     */
    @Transactional
    public UserDto updateUser(
            UserDetails userDetails,
            @Parameter(description = "Новые данные пользователя") UpdateUser updateUser) {

        User user = checkUser(userDetails.getUsername());
        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Вы не можете изменить чужие данные");
        }
        userMapper.updateFromDto(updateUser, user);
        return userMapper.toDto(user);
    }

    /**
     * Возвращает информацию об авторизованном пользователе.
     *
     * @param userDetails данные авторизованного пользователя
     * @return {@link UserDto} с информацией о пользователе
     */
    @Transactional
    public UserDto infoAuthUser(UserDetails userDetails) {
        User user = checkUser(userDetails.getUsername());
        return userMapper.toDto(user);
    }

    /**
     * Меняет пароль авторизованного пользователя.
     *
     * @param newPasswordRequestDto запрос с текущим и новым паролем
     * @param userDetails          данные авторизованного пользователя
     * @throws AccessDeniedException       если email не совпадает с авторизованным
     * @throws InvalidOldPasswordException если текущий пароль неверен
     */
    @Transactional
    public void changePassword(NewPasswordRequestDto newPasswordRequestDto, UserDetails userDetails) {
        User user = checkUser(userDetails.getUsername());

        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Вы не можете изменить чужие данные");
        }

        if (!passwordEncoder.matches(newPasswordRequestDto.currentPassword(), user.getPassword())) {
            throw new InvalidOldPasswordException("Неверный текущий пароль");
        }

        user.setPassword(passwordEncoder.encode(newPasswordRequestDto.newPassword()));
        userRepository.save(user);
    }
}
