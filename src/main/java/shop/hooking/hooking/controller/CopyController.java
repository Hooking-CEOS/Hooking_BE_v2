package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
//import org.json.simple.parser.JSONParser;
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
    public HttpRes<List<CopyRes>> copyList(){
        List<CopyRes> copyRes = copyService.getCopyList();
        Collections.shuffle(copyRes);
        return new HttpRes<>(copyRes);
    }


    // 카피라이팅 검색 조회
    @GetMapping("/search")
    public HttpRes<List<CopyRes>> copySearchList(@RequestParam(name = "keyword") String q) {
        if (q.isEmpty()) {
            return new HttpRes<>(HttpStatus.BAD_REQUEST.value(),"검색 결과를 찾을 수 없습니다.");
        }


        MoodType moodType = MoodType.fromKeyword(q);
        if (moodType != null) {
            List<CopyRes> copyRes = copyService.selectMoodByQuery(q);
            Collections.shuffle(copyRes);
            return new HttpRes<>(copyRes);
        } else if (BrandType.containsKeyword(q)) {
            List<CopyRes> copyRes = copyService.selectBrandByQuery(q);
            Collections.shuffle(copyRes);
            return new HttpRes<>(copyRes);
        } else {
            List<CopyRes> copyRes = copyService.selectCopyByQuery(q);
            if (copyRes.isEmpty()){
                return new HttpRes<>(HttpStatus.BAD_REQUEST.value(),"검색 결과를 찾을 수 없습니다.");
            }
            Collections.shuffle(copyRes);
            return new HttpRes<>(copyRes);
        }
    }




    // 스크랩한 카피라이팅 조회
    @GetMapping("/scrap")
    public HttpRes<List<CopyRes>> copyScrapList(HttpServletRequest httpRequest){
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyRes> copyRes = copyService.getCopyScrapList(user);
        return new HttpRes<>(copyRes);
    }


    // 카피라이팅 스크랩
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
        return new HttpRes<>(results);
    }

}
