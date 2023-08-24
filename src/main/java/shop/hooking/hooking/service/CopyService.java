package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.config.enumtype.BrandType;
import shop.hooking.hooking.config.enumtype.MoodType;
import shop.hooking.hooking.config.jwt.JwtTokenProvider;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.request.CrawlingData;
import shop.hooking.hooking.dto.request.CrawlingReq;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.CopySearchRes;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.exception.*;
import shop.hooking.hooking.repository.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;


@Service
@RequiredArgsConstructor
public class CopyService {

    private final CardRepository cardRepository;
    private final BrandRepository brandRepository;
    private final ScrapRepository scrapRepository;
    private final CardJpaRepository cardJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;

    //상속 -> 일반 부모 , 카카오 자식
    public List<CopyRes> getCopyList(HttpServletRequest httpRequest, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Long[] brandIds = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L};
        List<CopyRes> tempCopyRes = new ArrayList<>();
        for (Long brandId : brandIds) {
            List<CopyRes> copyRes = getTopSixCopy(brandId);
            tempCopyRes.addAll(copyRes);
        }
        Collections.shuffle(tempCopyRes);
        List<CopyRes> resultCopyRes = getCopyByIndex(tempCopyRes, index);
        setScrapCnt(httpRequest, resultCopyRes);
        setIsScrap(user, resultCopyRes);
        return resultCopyRes;

    }




    public List<CopyRes> getCopyByIndex(List<CopyRes> copyResList, int index) {
        int startIndex = index * 30;
        int endIndex = Math.min(startIndex + 30, copyResList.size());
        if (startIndex >= endIndex) {
            throw new OutOfIndexException();
        }
        return copyResList.subList(startIndex, endIndex);
    }



    public void setScrapCnt(HttpServletRequest httpRequest, List<CopyRes> copyResList) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null) {
            for (CopyRes copyRes : copyResList) {
                copyRes.setScrapCnt(0);
            }
        }
    }

    public void setIsScrap(User user, List<CopyRes> copyResList) {
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);

        for (CopyRes copyRes : copyResList) {
            long cardId = copyRes.getId();
            boolean isScrapFound = scraps.stream().anyMatch(scrap -> scrap.getCard().getId() == cardId);
            copyRes.setIsScrap(isScrapFound ? 1 : 0);
        }
    }

    @Transactional
    public List<CopyRes> getTopSixCopy(Long brandId) {
        List<Card> cards = cardRepository.findTop6ByBrandIdOrderByCreatedAtDesc(brandId);
        List<CopyRes> copyResList = new ArrayList<>();

        for (Card card : cards) {
            CopyRes copyRes = createCopyRes(card);
            copyResList.add(copyRes);
        }

        return copyResList;
    }

    public CopySearchRes searchBrandList(HttpServletRequest httpRequest, String q, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        q = checkKeyword(q);

        List<CopyRes> brandCopyRes;

        if (BrandType.containsKeyword(q)) {
            brandCopyRes = cardJpaRepository.searchBrand(q);
            setScrapCnt(httpRequest, brandCopyRes);
            setIsScrap(user, brandCopyRes);
            Collections.shuffle(brandCopyRes);

            CopySearchRes brandSearchResult = createCopySearchResult("brand", q, brandCopyRes, index);
            return brandSearchResult;
        }

        // 검색 결과가 없다면
        throw new CardNotFoundException();
    }


    public String checkKeyword(String q) {
        if (q.equals("애프터블로우")) {
            q = "애프터 블로우";
        }
        return q;
    }

    public CopySearchRes searchMoodList(HttpServletRequest httpRequest, String q, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        List<CopyRes> moodCopyRes;

        if (MoodType.containsKeyword(q)) {
            moodCopyRes = cardJpaRepository.searchMood(q);
            setScrapCnt(httpRequest, moodCopyRes);
            setIsScrap(user, moodCopyRes);
            Collections.shuffle(moodCopyRes);

            CopySearchRes moodSearchResult = createCopySearchResult("mood", q, moodCopyRes, index);
            return moodSearchResult;
        }

        // 검색 결과가 없다면
        throw new CardNotFoundException();
    }



    public CopySearchRes searchCopyList(HttpServletRequest httpRequest, String q, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        List<CopyRes> textCopyRes;

        textCopyRes = cardJpaRepository.searchCopy(q);

        if (!textCopyRes.isEmpty()) {
            setScrapCnt(httpRequest, textCopyRes);
            setIsScrap(user, textCopyRes);
            Collections.shuffle(textCopyRes);

            CopySearchRes textSearchResult = createCopySearchResult("text", q, textCopyRes, index);
            return textSearchResult;
        }

        // 검색 결과가 없다면
        throw new CardNotFoundException();
    }



    @Transactional
    public List<CopyRes> getScrapList(HttpServletRequest httpRequest, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);
        List<CopyRes> scrapList = new ArrayList<>();

        for (Scrap scrap : scraps) { //10,20,30
            CopyRes copyRes = createScrapRes(scrap);
            scrapList.add(copyRes);
            copyRes.setScrapTime(scrap.getCreatedAt());
        }

        setIsScrap(user,scrapList);
        scrapList.sort(Comparator.comparing(CopyRes::getScrapTime).reversed()); // 최신순으로 정렬
        List<CopyRes> result = getCopyByIndex(scrapList, index);

        return result;
    }

    @Transactional
    public Long createScrap(HttpServletRequest httpRequest, CopyReq copyReq) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Card card = cardRepository.findCardById(copyReq.getCardId());

        if (hasScrapped(user, card)) {
            throw new DuplicateScrapException();
        }

        Long cardId = card.getId();
        saveCopy(user, card);

        return cardId;
    }

    private boolean hasScrapped(User user, Card card) {
        Scrap existingScrap = scrapRepository.findByUserAndCard(user, card);
        return existingScrap != null;
    }

    @Transactional
    public Long saveCopy(User user, Card card) {

        Scrap scrap = Scrap.builder()
                .user(user)
                .card(card)
                .build();

        Scrap savedScrap = scrapRepository.save(scrap);

        return savedScrap.getId();
    }



    @Transactional
    public Long deleteScrap(HttpServletRequest httpRequest, CopyReq copyReq) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Long cardId = copyReq.getCardId();
        Card card = cardRepository.findCardById(cardId);
        Scrap scrap = scrapRepository.findByUserAndCard(user, card);

        if (scrap != null) {
            scrap.setDeleteYn(1);
            return cardId;
        }

        return null;
    }

    @Transactional
    public CopyRes createCopyRes(Card card) {
        Long id = card.getId(); // id로 넘어옴
        List<Scrap> scraps = scrapRepository.findByCardId(id);

        return CopyRes.builder()
                .id(id)
                .brand(card.getBrand())
                .text(card.getText())
                .scrapCnt(scraps.size())
                .createdAt(card.getCreatedAt())
                .cardLink(card.getUrl())
                .build();
    }





    public CopySearchRes createCopySearchResult(String type, String keyword, List<CopyRes> copyResList, int index) {
        List<CopyRes> slicedCopyResList = getCopyByIndex(copyResList, index);

        return CopySearchRes.builder()
                .type(type)
                .keyword(keyword)
                .totalNum(copyResList.size())
                .data(slicedCopyResList)
                .build();
    }


    @Transactional
    public CopyRes createScrapRes(Scrap scrap) {
        Long id = scrap.getCard().getId();

        List<Scrap> scraps = scrapRepository.findByCardId(id);

        return CopyRes.builder()
                .id(id)
                .brand(scrap.getCard().getBrand())
                .text(scrap.getCard().getText())
                .scrapCnt(scraps.size())
                .createdAt(scrap.getCard().getCreatedAt())
                .cardLink(scrap.getCard().getUrl())

                .build();
    }

    public List<CopyRes> getCopyFilter(HttpServletRequest httpRequest, int index, CardSearchCondition condition) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyRes> result = cardJpaRepository.filter(condition);
        result = getCopyByIndex(result, index);
        setScrapCnt(httpRequest, result);
        setIsScrap(user,result);
        return result;
    }

    @Transactional
    public void saveCrawlingData(CrawlingReq crawlingReq) {
        List<CrawlingData> dataList = crawlingReq.getData();
        for (CrawlingData data : dataList) {
            Long brandId = data.getBrandId();
            Brand brand = brandRepository.findBrandById(brandId);

            Card card = Card.builder()
                    .text(data.getText())
                    .createdAt(data.getCreatedAt())
                    .brand(brand)
                    .url(data.getUrl())
                    .build();

            cardRepository.save(card);
        }
    }


}