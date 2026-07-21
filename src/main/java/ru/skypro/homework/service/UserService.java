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


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User checkUser(String userName) {
        return userRepository.findByEmail(userName).orElseThrow(() -> new EntityNotFoundException("user not found"));
    }

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

    @Transactional
    public UserDto infoAuthUser(UserDetails userDetails) {
        User user = checkUser(userDetails.getUsername());
        return userMapper.toDto(user);
    }


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
