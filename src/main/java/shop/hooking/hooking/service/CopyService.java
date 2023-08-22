package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.config.enumtype.BrandType;
import shop.hooking.hooking.config.enumtype.MoodType;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.HttpRes;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.request.CrawlingData;
import shop.hooking.hooking.dto.request.CrawlingReq;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.CopySearchRes;
import shop.hooking.hooking.dto.response.CopySearchResult;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.exception.CustomException;
import shop.hooking.hooking.exception.error.CardNotFoundException;
import shop.hooking.hooking.exception.error.ScrapDuplicateException;
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
    private final JwtTokenProvider jwtTokenProvider;


    public List<CopyRes> getCopyList(HttpServletRequest httpRequest, int index, int limit) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Long[] brandIds = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L};
        List<CopyRes> tempCopyRes = new ArrayList<>();
        for (Long brandId : brandIds) {
            List<CopyRes> copyRes = getTopSixCopy(brandId);
            tempCopyRes.addAll(copyRes);
        }
        Collections.shuffle(tempCopyRes);
        int startIndex = index * limit;
        List<CopyRes> resultCopyRes = getCopyByIndex(tempCopyRes, startIndex);
        setScrapCnt(httpRequest, resultCopyRes);
        setIsScrap(user, resultCopyRes);
        return resultCopyRes;
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

    public void setIsScrap(User user, List<CopyRes> copyResList) {
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);

        for (CopyRes copyRes : copyResList) {
            long cardId = copyRes.getId();
            boolean isScrapFound = scraps.stream().anyMatch(scrap -> scrap.getCard().getId() == cardId);
            copyRes.setIsScrap(isScrapFound ? 1 : 0);
        }
    }


    public CopySearchRes searchCopyList(HttpServletRequest httpRequest, String q, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        CopySearchRes response = new CopySearchRes();
        List<CopySearchResult> results = new ArrayList<>();
        Integer startIndex = index*30;

        q = checkKeyword(q);

        List<CopyRes> moodCopyRes;
        List<CopyRes> textCopyRes;
        List<CopyRes> brandCopyRes;

        textCopyRes = cardJpaRepository.searchCopy(q);

        if (MoodType.containsKeyword(q)) {
            moodCopyRes = cardJpaRepository.searchMood(q);
            setScrapCnt(httpRequest, moodCopyRes);
            Collections.shuffle(moodCopyRes);
            results.add(createCopySearchResult("mood", q, moodCopyRes, startIndex));
            if (!textCopyRes.isEmpty()) {
                setScrapCnt(httpRequest, textCopyRes);
                Collections.shuffle(textCopyRes);
                results.add(createCopySearchResult("copy", q, textCopyRes, startIndex));
            }
        } else if (BrandType.containsKeyword(q)) {
            brandCopyRes = cardJpaRepository.searchBrand(q);
            setScrapCnt(httpRequest, brandCopyRes);
            Collections.shuffle(brandCopyRes);
            results.add(createCopySearchResult("brand", q, brandCopyRes, startIndex));
        } else if (!textCopyRes.isEmpty()) {
            setScrapCnt(httpRequest, textCopyRes);
            Collections.shuffle(textCopyRes);
            results.add(createCopySearchResult("copy", q, textCopyRes, startIndex));
        }

        // 검색 결과가 없다면
        if (q.isEmpty() || results.isEmpty()) {
            response.setCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("검색 결과를 찾을 수 없습니다.");
            response.setData(results);
            return response;
        }

        // 검색 결과가 있다면
        response.setCode(HttpStatus.OK.value());
        response.setMessage("요청에 성공하였습니다.");
        response.setData(results);

        List<CopySearchResult> copySearchResults = response.getData();
        for(CopySearchResult copySearchResult : copySearchResults){
            List<CopyRes> copyRes = copySearchResult.getData();
            setIsScrap(user, copyRes);
        }

        if (response.getCode() == 404) {
            throw new CardNotFoundException();
        }
        return response;

    }

    public String checkKeyword(String q) {
        if (q.equals("애프터블로우")) {
            q = "애프터 블로우";
        }
        return q;
    }



    @Transactional
    public List<CopyRes> getScrapList(HttpServletRequest httpRequest, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);
        List<CopyRes> scrapList = new ArrayList<>();

        for (Scrap scrap : scraps) {
            CopyRes copyRes = createScrapRes(scrap);
            scrapList.add(copyRes);
        }

        int startIndex = index * 30;
        List<CopyRes> result = getCopyByIndex(scrapList, startIndex);
        for(CopyRes copyRes : result){
            Scrap scrap = scrapRepository.findByUserAndCardId(user, copyRes.getId());
            copyRes.setScrapTime(scrap.getCreatedAt());

        }
        Collections.sort(result);
        return result;
    }

    @Transactional
    public Long createScrap(HttpServletRequest httpRequest, CopyReq copyReq) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Card card = cardRepository.findCardById(copyReq.getCardId());
        Long cardId = card.getId();
        saveCopy(user, card);

        return cardId;
    }

    @Transactional
    public Long saveCopy(User user, Card card) {

        if(scrapRepository.existsByUserAndCard(user,card)){
            throw new ScrapDuplicateException();
        }

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
        int length = scraps.size();

        Brand brand = card.getBrand();
        String text = card.getText();
        String cardLink = card.getUrl();
        Integer scrapCnt = length;
        LocalDateTime createdAt = card.getCreatedAt();
        return new CopyRes(id, brand,text,scrapCnt,createdAt,cardLink);
    }



    public void setScrapCnt(HttpServletRequest httpRequest, List<CopyRes> copyResList) {
        String token = httpRequest.getHeader("X-AUTH-TOKEN");
        if (token == null) {
            for (CopyRes copyRes : copyResList) {
                copyRes.setScrapCnt(0);
            }
        }
    }


    public List<CopyRes> getCopyByIndex(List<CopyRes> copyResList, int startIndex) {
        int endIndex = Math.min(startIndex + 30, copyResList.size());
        return copyResList.subList(startIndex, endIndex);
    }


    public CopySearchResult createCopySearchResult(String type, String keyword, List<CopyRes> copyResList, int index) {
        CopySearchResult result = new CopySearchResult();
        result.setType(type);
        result.setKeyword(keyword);
        result.setTotalNum(copyResList.size());
        result.setData(getCopyByIndex(copyResList, index));
        return result;
    }



    @Transactional
    public CopyRes createScrapRes(Scrap scrap) {
        Long id = scrap.getCard().getId();

        List<Scrap> scraps = scrapRepository.findByCardId(id);
        int length = scraps.size();

        Brand brand = scrap.getCard().getBrand();
        String text = scrap.getCard().getText();
        String cardLink = scrap.getCard().getUrl();
        Integer scrapCnt = length;
        LocalDateTime createdAt = scrap.getCard().getCreatedAt();
        return new CopyRes(id, brand,text,scrapCnt,createdAt,cardLink);
    }

    public List<CopyRes> getCopyFilter(HttpServletRequest httpRequest, int index, CardSearchCondition condition) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyRes> result = cardJpaRepository.filter(condition);
        int startIndex = index * 30;
        result = getCopyByIndex(result, startIndex);
        setScrapCnt(httpRequest, result);
        setIsScrap(user,result);
        return result;
    }

    @Transactional
    public void saveCrawlingData(CrawlingReq crawlingReq) {
        List<CrawlingData> dataList = crawlingReq.getData();
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


}