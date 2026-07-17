package ru.skypro.homework.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAd;
import ru.skypro.homework.dto.ad.ExtendedAd;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.repository.AdRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;

    private final AdMapper adMapper;


    public AdsDto getListAd() {
        List<Ad> adList = adRepository.findAll();
        List<AdDto> adDto = adList.stream()
                .map(adMapper::toDto)
                .toList();
        return new AdsDto(adDto.size(), adDto);
    }

    @Transactional
    public AdDto createAd(CreateOrUpdateAd dto) { //TODO проверка создателя и добавление его в объявление

        Ad ad = adMapper.toEntity(dto);
        adRepository.save(ad);

        return adMapper.toDto(ad);
    }

    public ExtendedAd getAdBuId(Integer id) {

        return adMapper.toExtendedDto(adRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария")));
    }

    @Transactional
    public void deleteAd(Integer id) {
        Ad ad = checkAd(id);
        adRepository.delete(ad);
    }

    @Transactional
    public AdDto updateAd(Integer id, CreateOrUpdateAd dto) {
        Ad ad = checkAd(id);
        adMapper.updateAd(dto, ad);
        return adMapper.toDto(ad);
    }

    public AdsDto getListAdUserAuth() {  //TODO доделать когда будет готова авторизация
        List<Ad> adList = adRepository.findAll();
        List<AdDto> adDto = adList.stream()
                .map(adMapper::toDto)
                .toList();
        return new AdsDto(adDto.size(), adDto);
    }


    private Ad checkAd(Integer id) {
        return adRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария"));
    }
}
