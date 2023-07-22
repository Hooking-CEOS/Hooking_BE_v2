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
        Long[] brandIds = {2L, 3L, 4L, 12L, 15L, 17L, 21L, 24L, 25L, 28L};

        List<CopyRes> tempCopyRes = new ArrayList<>();

        for (Long brandId : brandIds) {
            List<CopyRes> copyRes = copyService.getCopyList(brandId);
            tempCopyRes.addAll(copyRes);
        }
        Collections.shuffle(tempCopyRes);
        tempCopyRes = getLimitedCopyRes(tempCopyRes,30);


        return new HttpRes<>(tempCopyRes);
    }

    private List<CopyRes> getLimitedCopyRes(List<CopyRes> copyResList, int limit){
        Collections.shuffle(copyResList);
        int endIndex = Math.min(limit, copyResList.size());
        return copyResList.subList(0,endIndex);
    }

    @GetMapping("/search")
    public CopySearchResponse copySearchList(@RequestParam(name = "keyword") String q) {
        CopySearchResponse response = new CopySearchResponse();
        List<CopySearchResult> results = new ArrayList<>();

        if (q.isEmpty()) { //검색 결과가 없다면
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("검색 결과를 찾을 수 없습니다.");
            response.setData(results);
            return response;
        }

        MoodType moodType = MoodType.fromKeyword(q);
        List<CopyRes> moodCopyRes = new ArrayList<>();
        List<CopyRes> textCopyRes = new ArrayList<>();
        List<CopyRes> brandCopyRes = new ArrayList<>();

        textCopyRes = copyService.selectCopyByQuery(q);

        if (moodType != null) {
            moodCopyRes = copyService.selectMoodByQuery(q);
            Collections.shuffle(moodCopyRes);
            CopySearchResult moodResult = createCopySearchResult(moodCopyRes);
            moodResult.setType("mood");
            moodResult.setKeyword(q);
            results.add(moodResult);

            if(!textCopyRes.isEmpty()){
                Collections.shuffle(textCopyRes);
                CopySearchResult copyResult = createCopySearchResult(textCopyRes);
                copyResult.setType("copy");
                copyResult.setKeyword(q);
                setIndicesForCopyRes(textCopyRes, q);
                results.add(copyResult);
            }
        } else if (BrandType.containsKeyword(q)) {
            brandCopyRes = copyService.selectBrandByQuery(q);
            Collections.shuffle(brandCopyRes);
            CopySearchResult brandResult = createCopySearchResult(brandCopyRes);
            brandResult.setType("brand");
            brandResult.setKeyword(q);
            results.add(brandResult);
        } else if (!textCopyRes.isEmpty()){
            Collections.shuffle(textCopyRes);
            CopySearchResult copyResult = createCopySearchResult(textCopyRes);
            copyResult.setType("copy");
            copyResult.setKeyword(q);
            setIndicesForCopyRes(textCopyRes, q);
            results.add(copyResult);
        }

        if (results.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("검색 결과를 찾을 수 없습니다.");
            response.setData(results);
            return response;
        }

        response.setCode(HttpStatus.OK.value());
        response.setMessage("요청에 성공하였습니다.");
        response.setData(results);
        return response;
    }

    private CopySearchResult createCopySearchResult(List<CopyRes> copyResList) {
        CopySearchResult result = new CopySearchResult();
        int endIndex = Math.min(30, copyResList.size());
        List<CopyRes> limitedCopyRes = copyResList.subList(0, endIndex);
        result.setData(limitedCopyRes);
        return result;
    }


    private void setIndicesForCopyRes(List<CopyRes> copyResList, String keyword) {
        for (CopyRes copyRes : copyResList) {
            String lowercaseText = copyRes.getText().toLowerCase();
            int index = lowercaseText.indexOf(keyword.toLowerCase());
            List<Integer> indices = new ArrayList<>();
            while (index != -1) {
                indices.add(index);
                index = lowercaseText.indexOf(keyword.toLowerCase(), index + 1);
            }
            copyRes.setIndex(indices);
        }
    }


    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("/scrap")
    public HttpRes<List<CopyRes>> copyScrapList(HttpServletRequest httpRequest) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyRes> copyRes = copyService.getCopyScrapList(user);

        int endIndex = Math.min(30, copyRes.size());
        List<CopyRes> limitedCopyRes = copyRes.subList(0, endIndex);

        return new HttpRes<>(limitedCopyRes);
    }


    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @PostMapping("/scrap")
    public HttpRes<String> copyScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq) throws IOException {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Card card = cardRepository.findCardById(copyReq.getCardId());
        boolean isScrap = copyService.saveCopy(user, card);
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

        return new HttpRes<>("크롤링 데이터가 저장되었습니다.");
    }


    @GetMapping("/filter")
    public HttpRes<List<CopyRes>> searchFilterCard(CardSearchCondition condition) {
        List<CopyRes> results = cardJpaRepository.search(condition);
        List<CopyRes> limitedResults = getLimitedCopyRes(results,30);

        return new HttpRes<>(limitedResults);
    }

}
