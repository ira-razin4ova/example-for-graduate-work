package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAd;
import ru.skypro.homework.dto.ad.ExtendedAd;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.util.SecurityUtils;

import java.util.List;

/**
 * Сервис для работы с объявлениями.
 * <p>
 * Содержит бизнес-логику по управлению объявлениями: создание, получение,
 * обновление, удаление, а также фильтрацию по автору.
 * </p>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserService userService;

    /**
     * Возвращает список всех объявлений.
     *
     * @return {@link AdsDto} со списком объявлений и их количеством
     */
    public AdsDto getListAd() {
        List<Ad> adList = adRepository.findAll();
        List<AdDto> adDto = adList.stream().map(adMapper::toDto).toList();
        return new AdsDto(adDto.size(), adDto);
    }

    /**
     * Создаёт новое объявление от имени авторизованного пользователя.
     *
     * @param dto         данные нового объявления
     * @param userDetails данные авторизованного пользователя
     * @return {@link AdDto} созданного объявления
     */
    @Transactional
    public AdDto createAd(CreateOrUpdateAd dto, UserDetails userDetails) {

        Ad ad = adMapper.toEntity(dto);
        ad.setAuthor(userService.checkUser(userDetails.getUsername()));
        adRepository.save(ad);

        return adMapper.toDto(ad);
    }

    /**
     * Возвращает расширенную информацию об объявлении по его ID.
     *
     * @param id ID объявления
     * @return {@link ExtendedAd} с детальной информацией
     * @throws jakarta.persistence.EntityNotFoundException если объявление не найдено
     */
    public ExtendedAd getAdBuId(Integer id) {
        return adMapper.toExtendedDto(adRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария")));
    }

    /**
     * Удаляет объявление по ID (только автор или администратор).
     *
     * @param id          ID объявления
     * @param userDetails данные авторизованного пользователя
     */
    @Transactional
    public void deleteAd(Integer id, UserDetails userDetails) {
        Ad ad = checkAd(id);
        SecurityUtils.checkModifyPermission(ad.getAuthor(), userDetails);
        adRepository.delete(ad);
    }

    /**
     * Обновляет объявление по ID (только автор или администратор).
     *
     * @param id          ID объявления
     * @param dto         новые данные объявления
     * @param userDetails данные авторизованного пользователя
     * @return {@link AdDto} с обновлёнными данными
     */
    @Transactional
    public AdDto updateAd(Integer id, CreateOrUpdateAd dto, UserDetails userDetails) {
        Ad ad = checkAd(id);
        SecurityUtils.checkModifyPermission(ad.getAuthor(), userDetails);
        adMapper.updateAd(dto, ad);
        return adMapper.toDto(ad);

    }

    /**
     * Возвращает список объявлений авторизованного пользователя.
     *
     * @param userDetails данные авторизованного пользователя
     * @return {@link AdsDto} с объявлениями пользователя
     */
    public AdsDto getListAdUserAuth(UserDetails userDetails) {
        List<Ad> adList = adRepository.findAllByAuthorEmail(userDetails.getUsername());
        List<AdDto> adDto = adList.stream().map(adMapper::toDto).toList();
        return new AdsDto(adDto.size(), adDto);
    }

    /**
     * Проверяет существование объявления по ID.
     *
     * @param id ID объявления
     * @return найденное объявление
     * @throws jakarta.persistence.EntityNotFoundException если объявление не найдено
     */
    private Ad checkAd(Integer id) {
        return adRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария"));
    }

}
