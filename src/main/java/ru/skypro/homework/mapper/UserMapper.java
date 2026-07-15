package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.dto.user.NewPasswordRequestDto;
import ru.skypro.homework.dto.user.UpdateUser;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(source = "username", target = "email")
    User toEntity(Register register);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateFromDto(UpdateUser updateUser, @MappingTarget User user);

    //@Mapping(source = "userName", target = "email")
    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateFromDto(NewPasswordRequestDto newPasswordRequestDto, @MappingTarget User user);

}
