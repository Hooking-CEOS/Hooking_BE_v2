package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.entity.Brand;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.Scrap;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.CustomException;
import shop.hooking.hooking.exception.ErrorCode;
import shop.hooking.hooking.repository.BrandRepository;
import shop.hooking.hooking.repository.CardRepository;
import shop.hooking.hooking.repository.ScrapRepository;
import shop.hooking.hooking.repository.UserRepository;

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

//    @Transactional
//    public List<CopyRes> selectCopyByQuery(String q) {
//        List<Card> cards = cardRepository.findByNameContains(q);
//        List<CopyRes> copyResList = new ArrayList<>();
//
//        for (Card card : cards) {
//            CopyRes copyRes = createCopyRes(card);
//            copyResList.add(copyRes);
//        }
//        return copyResList;
//    }

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
        Brand brand = card.getBrand();
        String text = card.getText();
        Integer scrapCnt = card.getScrapCnt();
        LocalDateTime createdAt = card.getCreatedAt();
        return new CopyRes(brand,text,scrapCnt,createdAt);
    }

    @Transactional
    public CopyRes createScrapRes(Scrap scrap) {
        Brand brand = scrap.getCard().getBrand();
        String text = scrap.getCard().getText();
        Integer scrapCnt = scrap.getCard().getScrapCnt();
        LocalDateTime createdAt = scrap.getCreatedAt();
        return new CopyRes(brand,text,scrapCnt,createdAt);
    }


    @Transactional
    public void saveCopy(User user, Card card) throws IOException {
        Scrap scrap = Scrap.builder()
                .user(user)
                .card(card)
                .build();

        scrapRepository.save(scrap);

    }




}
