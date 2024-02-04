package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.config.enumtype.BrandType;
import shop.hooking.hooking.config.enumtype.MoodType;
import shop.hooking.hooking.dto.response.CopyResDto;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.request.CopyReqDto;
import shop.hooking.hooking.dto.request.CrawlingData;
import shop.hooking.hooking.dto.request.CrawlingReqDto;
import shop.hooking.hooking.dto.response.CopySearchResDto;
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
    public List<CopyResDto> getCopyList(HttpServletRequest httpRequest, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Long[] brandIds = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L};
        List<CopyResDto> tempCopyRes = new ArrayList<>();
        for (Long brandId : brandIds) {
            List<CopyResDto> copyRes = getTopSixCopy(brandId);
            tempCopyRes.addAll(copyRes);
        }
        Collections.shuffle(tempCopyRes);
        List<CopyResDto> resultCopyRes = getCopyByIndex(tempCopyRes, index);
        setScrapCnt(httpRequest, resultCopyRes);
        setIsScrap(user, resultCopyRes);
        return resultCopyRes;

    }




    public List<CopyResDto> getCopyByIndex(List<CopyResDto> copyResList, int index) {
        int startIndex = index * 30;
        int endIndex = Math.min(startIndex + 30, copyResList.size());
        if (startIndex >= endIndex) {
            throw new OutOfIndexException();
        }
        return copyResList.subList(startIndex, endIndex);
    }



    public void setScrapCnt(HttpServletRequest httpRequest, List<CopyResDto> copyResList) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null) {
            for (CopyResDto copyRes : copyResList) {
                copyRes.setScrapCnt(0);
            }
        }
    }

    public void setIsScrap(User user, List<CopyResDto> copyResList) {
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);

        for (CopyResDto copyRes : copyResList) {
            long cardId = copyRes.getId();
            boolean isScrapFound = scraps.stream().anyMatch(scrap -> scrap.getCard().getId() == cardId);
            copyRes.setIsScrap(isScrapFound ? 1 : 0);
        }
    }

    @Transactional
    public List<CopyResDto> getTopSixCopy(Long brandId) {
        List<Card> cards = cardRepository.findTop6ByBrandIdOrderByCreatedAtDesc(brandId);
        List<CopyResDto> copyResList = new ArrayList<>();

        for (Card card : cards) {
            CopyResDto copyRes = createCopyRes(card);
            copyResList.add(copyRes);
        }

        return copyResList;
    }

    public CopySearchResDto searchBrandList(HttpServletRequest httpRequest, String q, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        q = checkKeyword(q);

        List<CopyResDto> brandCopyRes;

        if (BrandType.containsKeyword(q)) {
            brandCopyRes = cardJpaRepository.searchBrand(q);
            setScrapCnt(httpRequest, brandCopyRes);
            setIsScrap(user, brandCopyRes);
            Collections.shuffle(brandCopyRes);

            CopySearchResDto brandSearchResult = createCopySearchResult("brand", q, brandCopyRes, index);
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

    public CopySearchResDto searchMoodList(HttpServletRequest httpRequest, String q, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        List<CopyResDto> moodCopyRes;

        if (MoodType.containsKeyword(q)) {
            moodCopyRes = cardJpaRepository.searchMood(q);
            setScrapCnt(httpRequest, moodCopyRes);
            setIsScrap(user, moodCopyRes);
            Collections.shuffle(moodCopyRes);

            CopySearchResDto moodSearchResult = createCopySearchResult("mood", q, moodCopyRes, index);
            return moodSearchResult;
        }

        // 검색 결과가 없다면
        throw new CardNotFoundException();
    }



    public CopySearchResDto searchCopyList(HttpServletRequest httpRequest, String q, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        List<CopyResDto> textCopyRes;

        textCopyRes = cardJpaRepository.searchCopy(q);

        if (!textCopyRes.isEmpty()) {
            setScrapCnt(httpRequest, textCopyRes);
            setIsScrap(user, textCopyRes);
            Collections.shuffle(textCopyRes);

            CopySearchResDto textSearchResult = createCopySearchResult("text", q, textCopyRes, index);
            return textSearchResult;
        }

        // 검색 결과가 없다면
        throw new CardNotFoundException();
    }



    @Transactional
    public List<CopyResDto> getScrapList(HttpServletRequest httpRequest, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);
        List<CopyResDto> scrapList = new ArrayList<>();

        for (Scrap scrap : scraps) { //10,20,30
            if (scrap.getId() == null)
                throw new ScrapNotFoundException(); // 왜 안터지지...

            CopyResDto copyRes = createScrapRes(scrap);
            scrapList.add(copyRes);
            copyRes.setScrapTime(scrap.getCreatedAt());


            setIsScrap(user, scrapList);
            scrapList.sort(Comparator.comparing(CopyResDto::getScrapTime).reversed()); // 최신순으로 정렬
            List<CopyResDto> result = getCopyByIndex(scrapList, index);


            return result;
        }
        return scrapList;
    }
//
    @Transactional
    public Long createScrap(HttpServletRequest httpRequest, CopyReqDto copyReq) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        System.out.println(user.getEmail()+" createScrap");
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
    public Long deleteScrap(HttpServletRequest httpRequest, CopyReqDto copyReq) {
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
    public CopyResDto createCopyRes(Card card) {
        Long id = card.getId(); // id로 넘어옴
        List<Scrap> scraps = scrapRepository.findByCardId(id);

        return CopyResDto.builder()
                .id(id)
                .brand(card.getBrand())
                .text(card.getText())
                .scrapCnt(scraps.size())
                .createdAt(card.getCreatedAt())
                .cardLink(card.getUrl())
                .build();
    }





    public CopySearchResDto createCopySearchResult(String type, String keyword, List<CopyResDto> copyResList, int index) {
        List<CopyResDto> slicedCopyResList = getCopyByIndex(copyResList, index);

        return CopySearchResDto.builder()
                .type(type)
                .keyword(keyword)
                .totalNum(copyResList.size())
                .data(slicedCopyResList)
                .build();
    }


    @Transactional
    public CopyResDto createScrapRes(Scrap scrap) {
        Long id = scrap.getCard().getId();

        List<Scrap> scraps = scrapRepository.findByCardId(id);

        return CopyResDto.builder()
                .id(id)
                .brand(scrap.getCard().getBrand())
                .text(scrap.getCard().getText())
                .scrapCnt(scraps.size())
                .createdAt(scrap.getCard().getCreatedAt())
                .cardLink(scrap.getCard().getUrl())
                .build();
    }

    public List<CopyResDto> getCopyFilter(HttpServletRequest httpRequest, int index, CardSearchCondition condition) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyResDto> result = cardJpaRepository.filter(condition);
        result = getCopyByIndex(result, index);
        setScrapCnt(httpRequest, result);
        setIsScrap(user,result);
        return result;
    }

    @Transactional
    public void saveCrawlingData(CrawlingReqDto crawlingReq) {
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