package ru.skypro.homework.service;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private User checkUser (Integer id) {
        return userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("user not found"));
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param id         идентификатор пользователя
     * @param updateUser DTO с новыми данными
     * @return обновлённый DTO
     * @throws EntityNotFoundException если пользователь не найден
     */

    @Transactional
    public UserDto updateUser(
            @Parameter(description = "ID пользователя", example = "1") Integer id,
            @Parameter(description = "Новые данные пользователя") UpdateUser updateUser) {

        User user = checkUser(id);
        userMapper.updateFromDto(updateUser, user);
        return userMapper.toDto(user);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */

    @Transactional
    public void deleteUser(Integer id) {
        User user = checkUser(id);
        userRepository.delete(user);
    }

    //TODO изменение пароля когда будет готова авторизация

    //TODO "Получить информацию об авторизованном пользователе" когда будет готова авторизация
}
