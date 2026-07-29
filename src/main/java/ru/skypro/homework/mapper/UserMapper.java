package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.model.user.User;

/**
 * MapStruct-маппер для преобразования между сущностью {@link User} и DTO.
 * <p>
 * Выполняет преобразования для регистрации, обновления и отображения пользователя.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует DTO регистрации в сущность пользователя.
     *
     * @param register DTO с данными регистрации
     * @return новая сущность {@link User}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(source = "username", target = "email")
    User toEntity(Register register);

    /**
     * Частично обновляет существующего пользователя из DTO обновления.
     * Игнорирует null-поля, не меняет id, email, пароль, роль и image.
     *
     * @param updateUser DTO с новыми данными
     * @param user       целевая сущность для обновления
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateFromDto(UpdateUser updateUser, @MappingTarget User user);

    /**
     * Преобразует сущность пользователя в DTO для отображения.
     *
     * @param user сущность пользователя
     * @return {@link UserDto} с информацией о пользователе
     */
    UserDto toDto(User user);

}
