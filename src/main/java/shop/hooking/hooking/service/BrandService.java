package shop.hooking.hooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.response.BrandRes;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.ReviewRes;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;

    private final CardRepository cardRepository;

    private final HaveRepository haveRepository;

    private final MoodRepository moodRepository;

    private final ScrapRepository scrapRepository;

    public List<BrandRes.BrandDto> getBrandList(){
        List<BrandRes.BrandDto> brandDtoList = new ArrayList<>();
        List<Brand> brands = brandRepository.findAll();

        for( Brand brand : brands){
            Long brandId = brand.getId();
            List<Card> cards = cardRepository.findCardsByBrandId(brandId);
            Card randomCard = cards.get(0);

            List<Have> haves = haveRepository.findByBrandId(brandId);

            Mood moodZero = moodRepository.findMoodById(haves.get(0).getMood().getId());
            Mood moodOne = moodRepository.findMoodById(haves.get(1).getMood().getId());
            Mood moodTwo = moodRepository.findMoodById(haves.get(2).getMood().getId());


            brandDtoList.add(BrandRes.BrandDto.builder()
                    .brandId(brand.getId())
                    .brandName(brand.getBrandName())
                    .brandLink(brand.getBrandLink())
                    .randomCard(randomCard.getText())
                    .mood(Arrays.asList(moodOne.getMoodName(),moodZero.getMoodName(),moodTwo.getMoodName()))
                    .build());
        }
        return  brandDtoList;

    }


    public BrandRes.BrandDetailDto getOneBrand(Long id) {
        Brand brand = brandRepository.findBrandById(id);

        List<Card> cards = cardRepository.findCardsByBrandId(brand.getId());

        for(Card card : cards){
            Long cardId = card.getId();

            List<Scrap> scraps  = scrapRepository.findByCardId(cardId);
            int length = scraps.size();
            card.setScrapCnt(length);

        }

        List<String> cardTexts = new ArrayList<>();
        int maxCardCount = Math.min(cards.size(), 3);
        for (int i = 0; i < maxCardCount; i++) {
            Card card = cards.get(i);
            cardTexts.add(card.getText());
        }

        List<Have> haves = haveRepository.findByBrandId(brand.getId());

        Mood moodZero = moodRepository.findMoodById(haves.get(0).getMood().getId());
        Mood moodOne = moodRepository.findMoodById(haves.get(1).getMood().getId());
        Mood moodTwo = moodRepository.findMoodById(haves.get(2).getMood().getId());


        BrandRes.BrandDetailDto brandDetailDto = BrandRes.BrandDetailDto.builder()
                .brandId(brand.getId())
                .brandName(brand.getBrandName())
                .brandIntro(brand.getBrandIntro())
                .brandLink(brand.getBrandLink())
                .randomCard(cardTexts)
                .card(cards)
                .mood(Arrays.asList(moodOne.getMoodName(),moodZero.getMoodName(),moodTwo.getMoodName()))
                .build();

        return brandDetailDto;
    }

//    public boolean followBrand(Long brandId, User user){
//
//        Brand brand = brandRepository.findBrandById(brandId);
//
//        if(followRepository.existsByBrandAndUser(brand, user)){
//            return false;
//        }
//
//        Follow follow =Follow.builder()
//                .brand(brand)
//                .user(user)
//                .build();
//        followRepository.save(follow); // 데이터베이스에 저장
//
//        return true;
//    }

}
