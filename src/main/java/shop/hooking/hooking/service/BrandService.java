package shop.hooking.hooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;
import shop.hooking.hooking.dto.response.BrandResDto;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.exception.*;

import shop.hooking.hooking.repository.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final JwtTokenProvider jwtTokenProvider;

    private final BrandRepository brandRepository;

    private final CardRepository cardRepository;

    private final HaveRepository haveRepository;

    private final MoodRepository moodRepository;

    private final ScrapRepository scrapRepository;

    public List<BrandResDto.BrandDto> getBrandList(){
        List<BrandResDto.BrandDto> brandDtoList = new ArrayList<>();
        List<Brand> brands = brandRepository.findAll();

        for( Brand brand : brands){
            Long brandId = brand.getId();
            List<Card> cards = cardRepository.findCardsByBrandId(brandId);
            Card randomCard = cards.get(0);

            List<Have> haves = haveRepository.findByBrandId(brandId);

            Mood moodZero = moodRepository.findMoodById(haves.get(0).getMood().getId());
            Mood moodOne = moodRepository.findMoodById(haves.get(1).getMood().getId());
            Mood moodTwo = moodRepository.findMoodById(haves.get(2).getMood().getId());


            brandDtoList.add(BrandResDto.BrandDto.builder()
                    .brandId(brand.getId())
                    .brandName(brand.getBrandName())
                    .brandLink(brand.getBrandLink())
                    .randomCard(randomCard.getText())
                    .mood(Arrays.asList(moodOne.getMoodName(),moodZero.getMoodName(),moodTwo.getMoodName()))
                    .build());
        }
        return  brandDtoList;

    }


    public BrandResDto.BrandDetailDto getOneBrand(Long id) {
        Brand brand = brandRepository.findBrandById(id);

        List<Card> cards = cardRepository.findCardsByBrandId(brand.getId());
        List<BrandResDto.cardDto> cardDtos = new ArrayList<>();
        for(Card card : cards){
            BrandResDto.cardDto cardDto = BrandResDto.cardDto.builder()
                    .id(card.getId())
                    .brandName(card.getBrand().getBrandName())
                    .text(card.getText())
                    .createdAt(card.getCreatedAt())
                    .scrapCnt(card.getScrapCnt())
                    .cardLink(card.getUrl())
                    .build();
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


        BrandResDto.BrandDetailDto brandDetailDto = BrandResDto.BrandDetailDto.builder()
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

     public List<BrandResDto.cardDto> getLimitedCardsByIndex(List<BrandResDto.cardDto> cards, int index) {
         int startIndex = index * 30;
         int endIndex = Math.min(startIndex + 30, cards.size());
         if (startIndex >= endIndex) {
             throw new OutOfIndexException();
         }
         return cards.subList(startIndex, endIndex);
    }

    public void setIsScrapWithUser(User user, List<BrandResDto.cardDto> cardList) {
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);

        // cardList의 id와 scraps의 card_id를 비교하여 isScrap 값을 설정
        for (BrandResDto.cardDto cardDto : cardList) {
            long cardId = cardDto.getId();
            boolean isScrapFound = scraps.stream().anyMatch(scrap -> scrap.getCard().getId() == cardId);
            cardDto.setIsScrap(isScrapFound ? 1 : 0);
        }
    }


    public void setScrapCntWhenTokenNotProvided(HttpServletRequest httpRequest, List<BrandResDto.cardDto> cardList) {

        String token = httpRequest.getHeader("Authorization");
        if (token == null) {
            for (BrandResDto.cardDto cardDto : cardList) {
                cardDto.setScrapCnt(0);
            }
        }
    }


    public BrandResDto.BrandDetailDto getBrandDetail(HttpServletRequest httpRequest, Long brand_id, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        BrandResDto.BrandDetailDto brandDetailDto = getOneBrand(brand_id);

        List<BrandResDto.cardDto> cards = brandDetailDto.getCard();

        int startIndex = index * 30;
        List<BrandResDto.cardDto> resultCards = getLimitedCardsByIndex(cards, startIndex);

        brandDetailDto.setCard(resultCards);

        setIsScrapWithUser(user, resultCards);
        setScrapCntWhenTokenNotProvided(httpRequest, resultCards);

        return brandDetailDto;
    }

}
