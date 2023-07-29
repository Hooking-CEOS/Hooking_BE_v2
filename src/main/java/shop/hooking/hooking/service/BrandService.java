package shop.hooking.hooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.response.BrandRes;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.ReviewRes;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.repository.*;

import javax.servlet.http.HttpServletRequest;
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
        List<BrandRes.cardDto> cardDtos = new ArrayList<>();
        for(Card card : cards){
            BrandRes.cardDto cardDto = new BrandRes.cardDto();
            cardDto.setId(card.getId());
            cardDto.setBrandName(card.getBrand().getBrandName());
            cardDto.setText(card.getText());
            cardDto.setCreatedAt(card.getCreatedAt());
            cardDto.setScrapCnt(card.getScrapCnt());
            cardDtos.add(cardDto);
        }


        cards.forEach(card -> card.setScrapCnt((int) scrapRepository.findByCardId(card.getId()).stream().count()));


        List<String> cardTexts = new ArrayList<>();
        int maxCardCount = Math.min(cards.size(), 3);
        for (int i = 0; i < maxCardCount; i++) {
            Card card = cards.get(i);
            cardTexts.add(card.getText());
        }

        List<Have> haves = haveRepository.findByBrandId(id);

        Mood moodZero = moodRepository.findMoodById(haves.get(0).getMood().getId());
        Mood moodOne = moodRepository.findMoodById(haves.get(1).getMood().getId());
        Mood moodTwo = moodRepository.findMoodById(haves.get(2).getMood().getId());


        BrandRes.BrandDetailDto brandDetailDto = BrandRes.BrandDetailDto.builder()
                .brandId(id)
                .brandName(brand.getBrandName())
                .brandIntro(brand.getBrandIntro())
                .brandLink(brand.getBrandLink())
                .randomCard(cardTexts)
                .card(cardDtos)
                .mood(Arrays.asList(moodOne.getMoodName(),moodZero.getMoodName(),moodTwo.getMoodName()))
                .build();

        return brandDetailDto;
    }

     public List<BrandRes.cardDto> getLimitedCardsByIndex(List<BrandRes.cardDto> cards, int startIndex) {
        int endIndex = Math.min(startIndex + 30, cards.size());
        return cards.subList(startIndex, endIndex);
    }

    public void setScrapCntWhenTokenNotProvided(HttpServletRequest httpRequest, List<BrandRes.cardDto> cardList) {

        String token = httpRequest.getHeader("X-AUTH-TOKEN");
        if (token == null) {
            for (BrandRes.cardDto cardDto : cardList) {
                cardDto.setScrapCnt(0);
            }
        }
    }

    public void setIsScrapWithUser(User user, List<BrandRes.cardDto> cardList) {
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);

        // cardList의 id와 scraps의 card_id를 비교하여 isScrap 값을 설정
        for (BrandRes.cardDto cardDto : cardList) {
            long cardId = cardDto.getId();
            boolean isScrapFound = scraps.stream().anyMatch(scrap -> scrap.getCard().getId() == cardId);
            cardDto.setIsScrap(isScrapFound ? 1 : 0);
        }
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
