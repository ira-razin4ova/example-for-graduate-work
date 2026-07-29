package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAd;
import ru.skypro.homework.dto.ad.ExtendedAd;
import ru.skypro.homework.model.Ad;

/**
 * MapStruct-маппер для преобразования между сущностью {@link Ad} и DTO.
 * <p>
 * Выполняет преобразования для создания, обновления, отображения в списке
 * и получения расширенной информации об объявлении.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface AdMapper {

    /**
     * Преобразует DTO создания объявления в сущность.
     *
     * @param ad DTO с данными объявления
     * @return новая сущность {@link Ad}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "author", ignore = true)
    Ad toEntity(CreateOrUpdateAd ad);

    /**
     * Преобразует сущность в DTO для отображения в списке.
     * Маппит id → pk и author.id → author.
     *
     * @param entity сущность объявления
     * @return {@link AdDto} для списка
     */
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    AdDto toDto(Ad entity);

    /**
     * Преобразует сущность в расширенное DTO с полными данными автора.
     *
     * @param entity сущность объявления
     * @return {@link ExtendedAd} с детальной информацией
     */
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "author.email", target = "email")
    @Mapping(source = "author.phone", target = "phone")
    ExtendedAd toExtendedDto(Ad entity);

    /**
     * Частично обновляет существующее объявление из DTO.
     * Игнорирует null-поля, не меняет id, image и author.
     *
     * @param createOrUpdateAd DTO с новыми данными
     * @param ad               целевая сущность для обновления
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "author", ignore = true)
    void updateAd(CreateOrUpdateAd createOrUpdateAd, @MappingTarget Ad ad);
}
