package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.config.BrandType;
import shop.hooking.hooking.config.MoodType;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.request.CrawlingData;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.CopySearchRes;
import shop.hooking.hooking.dto.response.CopySearchResult;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.exception.CustomException;
import shop.hooking.hooking.exception.ErrorCode;
import shop.hooking.hooking.repository.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    private final CardJpaRepository cardJpaRepository;


    @Transactional
    public List<CopyRes> getCopyList(Long brandId) {
        List<Card> cards = cardRepository.findTop6ByBrandIdOrderByCreatedAtDesc(brandId);
        List<CopyRes> copyResList = new ArrayList<>();

        for (Card card : cards) {
            CopyRes copyRes = createCopyRes(card);
            copyResList.add(copyRes);
        }

        return copyResList;
    }


    @Transactional

    public void saveCrawlingData(List<CrawlingData> dataList) {
        for (CrawlingData data : dataList) {
            String text = data.getText();
            String url = data.getUrl();
            LocalDateTime createdAt = data.getCreatedAt();
            Long brandId = data.getBrandId();

            Brand brand = brandRepository.findBrandById(brandId);

            Card card = new Card();
            card.setText(text);
            card.setCreatedAt(createdAt);
            card.setBrand(brand);
            card.setUrl(url);

            cardRepository.save(card);
        }
    }

    public CopySearchRes copySearchList(HttpServletRequest httpRequest, String q, int index) {
        CopySearchRes response = new CopySearchRes();
        List<CopySearchResult> results = new ArrayList<>();

        q = checkKeyword(q);

        List<CopyRes> moodCopyRes = new ArrayList<>();
        List<CopyRes> textCopyRes = new ArrayList<>();
        List<CopyRes> brandCopyRes = new ArrayList<>();

        textCopyRes = cardJpaRepository.searchCopy(q);

        if (MoodType.containsKeyword(q)) {
            moodCopyRes = cardJpaRepository.searchMood(q);
            setScrapCntWhenTokenNotProvided(httpRequest, moodCopyRes);
            Collections.shuffle(moodCopyRes);
            results.add(createCopySearchResult("mood", q, moodCopyRes, index));
            if (!textCopyRes.isEmpty()) {
                setScrapCntWhenTokenNotProvided(httpRequest, textCopyRes);
                Collections.shuffle(textCopyRes);
                results.add(createCopySearchResult("copy", q, textCopyRes, index));
            }
        } else if (BrandType.containsKeyword(q)) {
            brandCopyRes = cardJpaRepository.searchBrand(q);
            setScrapCntWhenTokenNotProvided(httpRequest, brandCopyRes);
            Collections.shuffle(brandCopyRes);
            results.add(createCopySearchResult("brand", q, brandCopyRes, index));
        } else if (!textCopyRes.isEmpty()) {
            setScrapCntWhenTokenNotProvided(httpRequest, textCopyRes);
            Collections.shuffle(textCopyRes);
            results.add(createCopySearchResult("copy", q, textCopyRes, index));
        }

        // 검색 결과가 없다면
        if (q.isEmpty() || results.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("검색 결과를 찾을 수 없습니다.");
            response.setData(results);
            return response;
        }

        // 검색 결과가 있다면
        response.setCode(HttpStatus.OK.value());
        response.setMessage("요청에 성공하였습니다.");
        response.setData(results);

        return response;
    }


    public List<CopyRes> getCopyScrapListAndSortByCreatedAt(HttpServletRequest httpRequest, int index, User user) {
        List<CopyRes> copyRes = getCopyScrapList(user);
        int startIndex = index * 30;
        List<CopyRes> resultCopyRes = getLimitedCopyResByIndex(copyRes, startIndex);
        resultCopyRes.sort((copy1, copy2) -> copy1.getCreatedAt().compareTo(copy2.getCreatedAt()));
        return resultCopyRes;
    }


    // 검색 쿼리
//    @Transactional
//    public List<CopyRes> selectCopyByQuery(String q) {
//        String type = "copy";
//
//        List<Card> cards = cardRepository.findByTextContaining(q);
//        List<CopyRes> copyResList = new ArrayList<>();
//
//        for (Card card : cards) {
//            CopyRes copyRes = createCopyRes(card);
//            copyResList.add(copyRes);
//        }
//        return copyResList;
//    }
//
//
//    @Transactional
//    public List<CopyRes> selectBrandByQuery(String q){
//        String type = "brand";
//
//        Brand brand = brandRepository.findBrandByBrandNameContaining(q);
//        List<Card> cards = cardRepository.findCardsByBrandId(brand.getId());
//        List<CopyRes> copyResList = new ArrayList<>();
//
//        for (Card card : cards) {
//            CopyRes copyRes = createCopyRes(card);
//            copyResList.add(copyRes);
//        }
//        return copyResList;
//    }
//
//    @Transactional
//    public List<CopyRes> selectMoodByQuery(String q){
//        String type = "mood";
//
//        Mood mood = moodRepository.findByMoodNameContaining(q);
//        List<Have> haves = haveRepository.findByMoodId(mood.getId());
//
//        List<CopyRes> copyResList = new ArrayList<>();
//
//        List<Brand> brands = new ArrayList<>();
//        for (Have have : haves) {
//            brands.add(have.getBrand());
//        }
//        for ( Brand brand : brands){
//            List<Card> cards = cardRepository.findCardsByBrandId(brand.getId());
//
//            for (Card card : cards) {
//                CopyRes copyRes = createCopyRes(card);
//                copyResList.add(copyRes);
//            }
//        }
//
//
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
        Long id = card.getId(); // id로 넘어옴

        List<Scrap> scraps = scrapRepository.findByCardId(id);
        int length = scraps.size();

        Brand brand = card.getBrand();
        String text = card.getText();
        Integer scrapCnt = length;
        LocalDateTime createdAt = card.getCreatedAt();
        return new CopyRes(id, brand,text,scrapCnt,createdAt);
    }

    public List<CopyRes> getCopyListFromBrandsAndSetScrapCnt(HttpServletRequest httpRequest, int index, int limit) {
        Long[] brandIds = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L};
        List<CopyRes> tempCopyRes = new ArrayList<>();
        for (Long brandId : brandIds) {
            List<CopyRes> copyRes = getCopyList(brandId);
            tempCopyRes.addAll(copyRes);
        }
        Collections.shuffle(tempCopyRes);
        int startIndex = index * limit;
        List<CopyRes> resultCopyRes = getLimitedCopyResByIndex(tempCopyRes, startIndex);
        setScrapCntWhenTokenNotProvided(httpRequest, resultCopyRes);
        return resultCopyRes;
    }




    public void setScrapCntWhenTokenNotProvided(HttpServletRequest httpRequest, List<CopyRes> copyResList) {
        String token = httpRequest.getHeader("X-AUTH-TOKEN");
        if (token == null) {
            for (CopyRes copyRes : copyResList) {
                copyRes.setScrapCnt(0);
            }
        }
    }


    public List<CopyRes> getLimitedCopyResByIndex(List<CopyRes> copyResList, int startIndex) {
        int endIndex = Math.min(startIndex + 30, copyResList.size());
        return copyResList.subList(startIndex, endIndex);
    }


    public CopySearchResult createCopySearchResult(String type, String keyword, List<CopyRes> copyResList, int index) {
        CopySearchResult result = new CopySearchResult();
        result.setType(type);
        result.setKeyword(keyword);
        result.setTotalNum(copyResList.size());
        result.setData(getLimitedCopyResByIndex(copyResList, index));
        return result;
    }

    public String checkKeyword(String q) {
        if (q.equals("애프터블로우")) {
            q = "애프터 블로우";
        }
        return q;
    }

    public ResponseEntity<CopySearchRes> getBadRequestResponseEntity(CopySearchRes response, List<CopySearchResult> results) {
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("검색 결과를 찾을 수 없습니다.");
        response.setData(results);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Transactional
    public CopyRes createScrapRes(Scrap scrap) {
        Long id = scrap.getCard().getId();

        List<Scrap> scraps = scrapRepository.findByCardId(id);
        int length = scraps.size();

        Brand brand = scrap.getCard().getBrand();
        String text = scrap.getCard().getText();
        Integer scrapCnt = length;
        LocalDateTime createdAt = scrap.getCard().getCreatedAt();
        return new CopyRes(id, brand,text,scrapCnt,createdAt);
    }

    public List<CopyRes> searchFilterCard(HttpServletRequest httpRequest, int index, CardSearchCondition condition) {
        List<CopyRes> results = cardJpaRepository.filter(condition);
        int startIndex = index * 30;
        List<CopyRes> resultCopyRes = getLimitedCopyResByIndex(results, startIndex);
        setScrapCntWhenTokenNotProvided(httpRequest, resultCopyRes);
        return resultCopyRes;
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

    @Transactional
    public boolean cancelScrap(User user, Card card) {
        Scrap scrap = scrapRepository.findByUserAndCard(user, card);

        if (scrap != null) {
            scrap.setDeleteYn(1);
            return true;
        }

        return false;
    }

}