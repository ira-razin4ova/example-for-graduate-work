package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.user.User;
@Component
public class UserMapper {

    public User toEntity(UpdateUser updateUser) {
        return User.builder()
                .username(updateUser.username())
                .password(updateUser.password())
                .firstName(updateUser.firstName())
                .lastName(updateUser.lastName())
                .phone(updateUser.phone())
                .role(updateUser.role())
                .build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }
}
