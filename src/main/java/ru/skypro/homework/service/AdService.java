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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserService userService;

    public AdsDto getListAd() {
        List<Ad> adList = adRepository.findAll();
        List<AdDto> adDto = adList.stream().map(adMapper::toDto).toList();
        return new AdsDto(adDto.size(), adDto);
    }

    @Transactional
    public AdDto createAd(CreateOrUpdateAd dto, UserDetails userDetails) {

        Ad ad = adMapper.toEntity(dto);
        ad.setAuthor(userService.checkUser(userDetails.getUsername()));
        adRepository.save(ad);

        return adMapper.toDto(ad);
    }

    public ExtendedAd getAdBuId(Integer id) {
        return adMapper.toExtendedDto(adRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария")));
    }

    @Transactional
    public void deleteAd(Integer id, UserDetails userDetails) {
        Ad ad = checkAd(id);
        SecurityUtils.checkModifyPermission(ad.getAuthor(), userDetails);
        adRepository.delete(ad);
    }

    @Transactional
    public AdDto updateAd(Integer id, CreateOrUpdateAd dto, UserDetails userDetails) {
        Ad ad = checkAd(id);
        SecurityUtils.checkModifyPermission(ad.getAuthor(), userDetails);
        adMapper.updateAd(dto, ad);
        return adMapper.toDto(ad);

    }

    public AdsDto getListAdUserAuth(UserDetails userDetails) {
        List<Ad> adList = adRepository.findAllByAuthorEmail(userDetails.getUsername());
        List<AdDto> adDto = adList.stream().map(adMapper::toDto).toList();
        return new AdsDto(adDto.size(), adDto);
    }


    private Ad checkAd(Integer id) {
        return adRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Неправильный идентификатор комментария"));
    }

}
