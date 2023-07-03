package shop.hooking.hooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.response.BrandRes;
import shop.hooking.hooking.dto.response.ReviewRes;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.repository.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;

    private final FollowRepository followRepository;

    private final CardRepository cardRepository;

    private final HaveRepository haveRepository;

    private final MoodRepository moodRepository;

    public List<BrandRes.BrandDto> getBrandList(){
        List<BrandRes.BrandDto> brandDtoList = new ArrayList<>();
        List<Brand> brands = brandRepository.findAll();// 데이터베이스에서 모든 브랜드 기본정보 가져옴
        //무드 테이블에서 분위기 3개
        //카드 테이블에서 카피라이팅 본문만 랜덤 1개 -> 완료
        for( Brand brand : brands){
            Long brandId = brand.getId();
            List<Card> cards = cardRepository.findCardsByBrandId(brandId);
            Card randomCard = cards.get(0); // 임의로 첫번재 카드 본문 가져오게 만듦

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
        //무드 테이블에서 분위기 3개
        //카드 테이블에서 카피라이팅 본문 랜덤 3개
        //카드 테이블에서 카피라이팅 전체 가져오기

        Brand brand = brandRepository.findBrandById(id);// brand 엔티티에서 4개(아이디, 이름, 한줄소개, 링크)만 가져옴

        List<Card> cards = cardRepository.findCardsByBrandId(brand.getId()); //카드 리스트 가져옴

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

    public void followBrand(Long brandId, User user){

        Brand brand = brandRepository.findBrandById(brandId);

        Follow follow =Follow.builder()
                .brand(brand)
                .user(user)
                .build();
        followRepository.save(follow); // 데이터베이스에 저장
    }
}
