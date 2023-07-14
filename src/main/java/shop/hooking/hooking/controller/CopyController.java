package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.config.BrandType;
import shop.hooking.hooking.config.MoodType;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.HttpRes;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.request.CrawlingData;
import shop.hooking.hooking.dto.request.CrawlingReq;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.CopySearchResponse;
import shop.hooking.hooking.dto.response.CopySearchResult;
import shop.hooking.hooking.entity.Brand;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.BrandRepository;
import shop.hooking.hooking.repository.CardJpaRepository;
import shop.hooking.hooking.repository.CardRepository;
import shop.hooking.hooking.service.CopyService;
import shop.hooking.hooking.service.JwtTokenProvider;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/copy")
public class CopyController {

    private final JwtTokenProvider jwtTokenProvider;

    private final CopyService copyService;

    private final CardRepository cardRepository;

    private final CardJpaRepository cardJpaRepository;

    private final BrandRepository brandRepository;

    // 전체 카피라이팅 조회
    @GetMapping("")
    public HttpRes<List<CopyRes>> copyList() {
        List<CopyRes> copyRes = copyService.getCopyList();
        Collections.shuffle(copyRes);

        int endIndex = Math.min(30, copyRes.size()); // 최대 30개까지만 반환
        List<CopyRes> limitedCopyRes = copyRes.subList(0, endIndex);

        return new HttpRes<>(limitedCopyRes);
    }


    @GetMapping("/search")
    public CopySearchResponse copySearchList(@RequestParam(name = "keyword") String q) {
        CopySearchResponse response = new CopySearchResponse();
        List<CopySearchResult> results = new ArrayList<>();

        if (q.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("검색 결과를 찾을 수 없습니다.");
            response.setData(results);
            return response;
        }

        MoodType moodType = MoodType.fromKeyword(q);
        if (moodType != null) { // 무드 키워드에 속한다
            List<CopyRes> moodCopyRes = copyService.selectMoodByQuery(q);
            Collections.shuffle(moodCopyRes);

            CopySearchResult moodResult = new CopySearchResult();
            moodResult.setType("mood");

            int endIndex = Math.min(30, moodCopyRes.size()); // 최대 30개까지만 반환
            List<CopyRes> limitedMoodCopyRes = moodCopyRes.subList(0, endIndex);
            moodResult.setData(limitedMoodCopyRes);

            results.add(moodResult);

            List<CopyRes> copyCopyRes = copyService.selectCopyByQuery(q);
            if (!copyCopyRes.isEmpty()) {
                CopySearchResult copyResult = new CopySearchResult();
                setIndicesForCopyRes(copyCopyRes, q);
                copyResult.setType("copy");

                int copyEndIndex = Math.min(30, copyCopyRes.size()); // 최대 30개까지만 반환
                List<CopyRes> limitedCopyCopyRes = copyCopyRes.subList(0, copyEndIndex);
                copyResult.setData(limitedCopyCopyRes);

                results.add(copyResult);
            }
        } else if (BrandType.containsKeyword(q)) { // 브랜드에 속한다
            List<CopyRes> copyRes = copyService.selectBrandByQuery(q);
            Collections.shuffle(copyRes);
            CopySearchResult result = new CopySearchResult();
            result.setType("brand");

            int endIndex = Math.min(30, copyRes.size()); // 최대 30개까지만 반환
            List<CopyRes> limitedCopyRes = copyRes.subList(0, endIndex);
            result.setData(limitedCopyRes);

            results.add(result);
        } else {
            List<CopyRes> copyRes = copyService.selectCopyByQuery(q);
            Collections.shuffle(copyRes);
            if (!copyRes.isEmpty()) {
                CopySearchResult result = new CopySearchResult();
                setIndicesForCopyRes(copyRes, q);
                result.setType("copy");

                int endIndex = Math.min(30, copyRes.size()); // 최대 30개까지만 반환
                List<CopyRes> limitedCopyRes = copyRes.subList(0, endIndex);
                result.setData(limitedCopyRes);

                results.add(result);
            }
        }

        response.setCode(HttpStatus.OK.value());
        response.setMessage("요청에 성공하였습니다.");
        response.setData(results);
        return response;
    }


    private void setIndicesForCopyRes(List<CopyRes> copyResList, String keyword) {
        for (CopyRes copyRes : copyResList) {
            String lowercaseText = copyRes.getText().toLowerCase(); // 소문자로 변환
            int index = lowercaseText.indexOf(keyword.toLowerCase()); // 첫번째 키워드의 위치 인덱스를 찾은 후,
            List<Integer> indices = new ArrayList<>();
            while (index != -1) { // index가 -1이 아닐 때까지
                indices.add(index);
                index = lowercaseText.indexOf(keyword.toLowerCase(), index + 1); // 키워드 위치 인덱스를 indices에 추가
            }
            copyRes.setIndex(indices);
        }
    }







    // 스크랩한 카피라이팅 조회
    @GetMapping("/scrap")
    public HttpRes<List<CopyRes>> copyScrapList(HttpServletRequest httpRequest) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyRes> copyRes = copyService.getCopyScrapList(user);

        int endIndex = Math.min(30, copyRes.size()); // 최대 30개까지만 반환
        List<CopyRes> limitedCopyRes = copyRes.subList(0, endIndex);

        return new HttpRes<>(limitedCopyRes);
    }



    // 카피라이팅 스크랩
    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @PostMapping("/scrap")
    public HttpRes<String> copyScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq) throws IOException {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Card card = cardRepository.findCardById(copyReq.getCardId());
        boolean isScrap = copyService.saveCopy(user, card); // 스크랩됐으면->true, 안됐으면->false
        if(isScrap){
            return new HttpRes<>("스크랩을 완료하였습니다.");
        }

        return new HttpRes<>(HttpStatus.BAD_REQUEST.value(),"중복 스크랩이 불가능합니다.");
    }



    @PostMapping("/crawling")
    public HttpRes<String> saveCrawling(@RequestBody CrawlingReq crawlingReq) {
        List<CrawlingData> dataList = crawlingReq.getData();


        for (CrawlingData data : dataList) {
            String text = data.getText();
            LocalDateTime createdAt = data.getCreatedAt();
            Long brandId = data.getBrandId();

            Brand brand = brandRepository.findBrandById(brandId);

            Card card = new Card();


            // 'text'와 'createdAt' 값을 데이터베이스에 저장
            card.setText(text);
            card.setCreatedAt(createdAt);
            card.setBrand(brand);

            cardRepository.save(card);
        }

        return new HttpRes<>("크롤링 데이터가 저장되었습니다.");
    }


    //카피라이팅 필터
    @GetMapping("/filter")
    public HttpRes<List<CopyRes>> searchFilterCard(CardSearchCondition condition) {
        List<CopyRes> results = cardJpaRepository.search(condition);
        Collections.shuffle(results);

        int endIndex = Math.min(30, results.size()); // 최대 30개까지만 반환
        List<CopyRes> limitedResults = results.subList(0, endIndex);

        return new HttpRes<>(limitedResults);
    }

}
