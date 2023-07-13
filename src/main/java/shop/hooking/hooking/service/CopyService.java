package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.exception.CustomException;
import shop.hooking.hooking.exception.ErrorCode;
import shop.hooking.hooking.repository.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CopyService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BrandRepository brandRepository;
    private final ScrapRepository scrapRepository;
    private final MoodRepository moodRepository;
    private final HaveRepository haveRepository;


    @Transactional
    public List<CopyRes> getCopyList() {
        List<Card> cards = cardRepository.findAll(); //카피 다가져옴
        List<CopyRes> copyResList = new ArrayList<>();

        for (Card card : cards) {
            CopyRes copyRes = createCopyRes(card); //하나씩 card 객체 생성
            copyResList.add(copyRes); //하나씩 cardRes를 List에 add
        }

        return copyResList;
    }

    @Transactional
    public List<CopyRes> selectCopyByQuery(String q) {
        String type = "copy";

        List<Card> cards = cardRepository.findByTextContaining(q);
        List<CopyRes> copyResList = new ArrayList<>();

        for (Card card : cards) {
            CopyRes copyRes = createCopyRes(card);
            copyResList.add(copyRes);
        }
        return copyResList;
    }


    @Transactional
    public List<CopyRes> selectBrandByQuery(String q){
        String type = "brand";

        Brand brand = brandRepository.findBrandByBrandNameContaining(q);
        List<Card> cards = cardRepository.findCardsByBrandId(brand.getId());
        List<CopyRes> copyResList = new ArrayList<>();

        for (Card card : cards) {
            CopyRes copyRes = createCopyRes(card);
            copyResList.add(copyRes);
        }
        return copyResList;
    }

    @Transactional
    public List<CopyRes> selectMoodByQuery(String q){
        String type = "mood";

        Mood mood = moodRepository.findByMoodNameContaining(q);
        List<Have> haves = haveRepository.findByMoodId(mood.getId());

        List<CopyRes> copyResList = new ArrayList<>();

        List<Brand> brands = new ArrayList<>();
        for (Have have : haves) {
            brands.add(have.getBrand()); // 브랜드 다 가져오고
        }
        for ( Brand brand : brands){
            List<Card> cards = cardRepository.findCardsByBrandId(brand.getId()); // 카드 다 가져오고

            for (Card card : cards) {
                CopyRes copyRes = createCopyRes(card);
                copyResList.add(copyRes); // 리스트 형태로 반환
            }
        }


        return copyResList;
    }


    @Transactional
    public List<CopyRes> getCopyScrapList(User user) {
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);
        List<CopyRes> scrapResList = new ArrayList<>();

        for (Scrap scrap : scraps) {
            CopyRes copyRes = createScrapRes(scrap);
            scrapResList.add(copyRes);
        }

        return scrapResList;
    }

    @Transactional
    public CopyRes createCopyRes(Card card) {
        Long id = card.getId(); // id로 넘어옴

        List<Scrap> scraps = scrapRepository.findByCardId(id); // 스크랩 객체들 모두 불러옴
        int length = scraps.size();

        Brand brand = card.getBrand();
        String text = card.getText();
        Integer scrapCnt = length;
        LocalDateTime createdAt = card.getCreatedAt();
        return new CopyRes(id, brand,text,scrapCnt,createdAt);
    }

    @Transactional
    public CopyRes createScrapRes(Scrap scrap) {
        Long id = scrap.getCard().getId(); // cardId

        List<Scrap> scraps = scrapRepository.findByCardId(id); // 스크랩 객체들 모두 불러옴
        int length = scraps.size();

        Brand brand = scrap.getCard().getBrand();
        String text = scrap.getCard().getText();
        Integer scrapCnt = length;
        LocalDateTime createdAt = scrap.getCard().getCreatedAt();
        return new CopyRes(id, brand,text,scrapCnt,createdAt);
    }


    @Transactional
    public boolean saveCopy(User user, Card card) throws IOException {

        if(scrapRepository.existsByUserAndCard(user,card)){
            return false;
        }
        Scrap scrap = Scrap.builder()
                .user(user)
                .card(card)
                .build();

        scrapRepository.save(scrap);

        return true;
    }




}