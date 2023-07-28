package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.config.BrandType;
import shop.hooking.hooking.config.MoodType;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.HttpRes;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.request.CrawlingData;
import shop.hooking.hooking.dto.request.CrawlingReq;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.CopySearchRes;
import shop.hooking.hooking.dto.response.CopySearchResult;
import shop.hooking.hooking.entity.Brand;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.BrandRepository;
import shop.hooking.hooking.repository.CardJpaRepository;
import shop.hooking.hooking.repository.CardRepository;
import shop.hooking.hooking.service.CopyService;
import shop.hooking.hooking.service.JwtTokenProvider;
import springfox.documentation.annotations.Cacheable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Cacheable("copyListCache")
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
    // 페이지네이션 구현
    @GetMapping("/{index}") //copy/0=> 0-30 copy/1=>0~30 copy/2=>60~90 copy/9 => 270~300
    public ResponseEntity<HttpRes<List<CopyRes>>> copyList(HttpServletRequest httpRequest, @PathVariable int index) {
        Long[] brandIds = {2L, 3L, 4L, 12L, 15L, 17L, 21L, 24L, 25L, 28L};

        List<CopyRes> tempCopyRes = new ArrayList<>();

        for (Long brandId : brandIds) {
            List<CopyRes> copyRes = copyService.getCopyList(brandId); //10개씩 -> 100개
            tempCopyRes.addAll(copyRes);
        }

        // 랜덤
        Collections.shuffle(tempCopyRes);

        if (tempCopyRes.isEmpty()) {
            String errorMessage = "검색 결과를 찾을 수 없습니다.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // 인덱싱
        int startIndex = index * 30;
        List<CopyRes> resultCopyRes = getLimitedCopyResByIndex(tempCopyRes, startIndex);
        setScrapCntWhenTokenNotProvided(httpRequest, resultCopyRes);
        return ResponseEntity.ok(new HttpRes<>(resultCopyRes));
    }



    @GetMapping("/search/{index}")
    public ResponseEntity<CopySearchRes> copySearchList(HttpServletRequest httpRequest,
                                                        @RequestParam(name = "keyword") String q,
                                                        @PathVariable int index) {



        CopySearchRes response = new CopySearchRes();
        List<CopySearchResult> results = new ArrayList<>();

        if (q.isEmpty()) { // 검색 결과가 없다면
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("검색 결과를 찾을 수 없습니다.");
            response.setData(results);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        q = checkKeyword(q);


        // 검색 결과 처리 로직...
        MoodType moodType = MoodType.fromKeyword(q);
        List<CopyRes> moodCopyRes = new ArrayList<>();
        List<CopyRes> textCopyRes = new ArrayList<>();
        List<CopyRes> brandCopyRes = new ArrayList<>();

        textCopyRes = cardJpaRepository.searchCopy(q);

        if (moodType != null) {
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
        if (results.isEmpty()) {
            return getBadRequestResponseEntity(response, results);
        }


        response.setCode(HttpStatus.OK.value());
        response.setMessage("요청에 성공하였습니다.");
        response.setData(results);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);

    }



    private List<CopyRes> getLimitedCopyResByIndex(List<CopyRes> copyResList, int startIndex) {
        int endIndex = Math.min(startIndex + 30, copyResList.size());
        return copyResList.subList(startIndex, endIndex);
    }




    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("/scrap/{index}")
    public ResponseEntity<HttpRes<List<CopyRes>>> copyScrapList(HttpServletRequest httpRequest, @PathVariable int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyRes> copyRes = copyService.getCopyScrapList(user);

        int startIndex = index * 30;
        List<CopyRes> resultCopyRes = getLimitedCopyResByIndex(copyRes, startIndex);

        return ResponseEntity.ok(new HttpRes<>(resultCopyRes));
    }



    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @PostMapping("/scrap")
    public ResponseEntity<HttpRes<String>> copyScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq) throws IOException {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Card card = cardRepository.findCardById(copyReq.getCardId());
        boolean isScrap = copyService.saveCopy(user, card);

        if (isScrap) {
            return ResponseEntity.ok(new HttpRes<>("스크랩을 완료하였습니다."));
        } else {
            String errorMessage = "중복 스크랩이 불가능합니다.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpRes<>(HttpStatus.BAD_REQUEST.value(), errorMessage));
        }
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


    @GetMapping("/filter/{index}")
    public ResponseEntity<List<CopyRes>> searchFilterCard(HttpServletRequest httpRequest,@PathVariable int index,CardSearchCondition condition) {
        List<CopyRes> results = cardJpaRepository.filter(condition);
        int startIndex = index * 30; //인덱싱
        List<CopyRes> resultCopyRes = getLimitedCopyResByIndex(results, startIndex);
        setScrapCntWhenTokenNotProvided(httpRequest, resultCopyRes);
        if(resultCopyRes.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultCopyRes);
    }

    private String checkKeyword(String q) {
        if (q.equals("애프터블로우")) {
            q = "애프터 블로우";
        }
        return q;
    }


    private void setScrapCntWhenTokenNotProvided(HttpServletRequest httpRequest, List<CopyRes> copyResList) {
        String token = httpRequest.getHeader("X-AUTH-TOKEN");
        if (token == null) {
            for (CopyRes copyRes : copyResList) {
                copyRes.setScrapCnt(0);
            }
        }
    }


    private CopySearchResult createCopySearchResult(String type, String keyword, List<CopyRes> copyResList, int index) {
        CopySearchResult result = new CopySearchResult();
        result.setType(type);
        result.setKeyword(keyword);
        result.setTotalNum(copyResList.size());
        result.setData(getLimitedCopyResByIndex(copyResList, index));
        return result;
    }

    private ResponseEntity<CopySearchRes> getBadRequestResponseEntity(CopySearchRes response, List<CopySearchResult> results) {
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("검색 결과를 찾을 수 없습니다.");
        response.setData(results);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    private List<CopyRes> getLimitedCopyRes(List<CopyRes> copyResList, int limit){
        Collections.shuffle(copyResList);
        int endIndex = Math.min(limit, copyResList.size());
        return copyResList.subList(0,endIndex);
    }

    @PostMapping ("/scrap/cancel")
    public ResponseEntity<String> cancelScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq){
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Long cardId = copyReq.getCardId();
        Card card = cardRepository.findCardById(cardId);
        boolean is_canceled = copyService.cancelScrap(user, card);
        if(is_canceled){
            return ResponseEntity.status(HttpStatus.OK).body("삭제되었습니다.");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("스크랩 정보가 유효하지 않습니다. ");
        }

    }


}