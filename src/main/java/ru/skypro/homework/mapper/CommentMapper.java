package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.model.Comment;

/**
 * MapStruct-маппер для преобразования между сущностью {@link Comment} и DTO.
 * <p>
 * Выполняет преобразования для создания и отображения комментариев.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Преобразует DTO создания комментария в сущность.
     *
     * @param comment DTO с текстом комментария
     * @return новая сущность {@link Comment}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toEntity(CreateOrUpdateComment comment);

    /**
     * Преобразует сущность комментария в DTO для отображения.
     * Маппит id → pk и поля автора.
     *
     * @param entity сущность комментария
     * @return {@link CommentDto} с информацией о комментарии
     */
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.image", target = "authorImage")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    CommentDto toDto(Comment entity);

}
