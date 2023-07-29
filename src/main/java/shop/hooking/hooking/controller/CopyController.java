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
import shop.hooking.hooking.entity.Scrap;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.BrandRepository;
import shop.hooking.hooking.repository.CardJpaRepository;
import shop.hooking.hooking.repository.CardRepository;
import shop.hooking.hooking.repository.ScrapRepository;
import shop.hooking.hooking.service.CopyService;
import shop.hooking.hooking.service.JwtTokenProvider;
import springfox.documentation.annotations.Cacheable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Cacheable("copyListCache")
@RestController
@RequiredArgsConstructor
@RequestMapping("/copy")
public class CopyController {

    private final JwtTokenProvider jwtTokenProvider;

    private final CopyService copyService;

    private final CardRepository cardRepository;

    private final ScrapRepository scrapRepository;




    // 전체 카피라이팅 조회
    // 페이지네이션
    @GetMapping("/{index}")
    public ResponseEntity<HttpRes<List<CopyRes>>> copyList(HttpServletRequest httpRequest, @PathVariable int index) {

        int limit = 30;
        List<CopyRes> resultCopyRes = copyService.getCopyListFromBrandsAndSetScrapCnt(httpRequest,index,limit);
        return ResponseEntity.ok(new HttpRes<>(resultCopyRes));

    }


    // 카피라이팅 검색
    @GetMapping("/search/{index}")
    public ResponseEntity<CopySearchRes> copySearchList(HttpServletRequest httpRequest,
                                                        @RequestParam(name = "keyword") String q,
                                                        @PathVariable int index) {
        CopySearchRes response = copyService.copySearchList(httpRequest, q, index);
        if (response.getCode() == HttpStatus.BAD_REQUEST.value()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }



    // 카피라이팅 스크랩 가져오기
    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("/scrap/{index}")
    public ResponseEntity<HttpRes<List<CopyRes>>> copyScrapList(HttpServletRequest httpRequest, @PathVariable int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyRes> copyRes = copyService.getCopyScrapList(user);
        int startIndex = index * 30;
        List<CopyRes> resultCopyRes = copyService.getLimitedCopyResByIndex(copyRes, startIndex);
        for(CopyRes copyRes1 : resultCopyRes){
            Scrap scrap = scrapRepository.findByUserAndCardId(user, copyRes1.getId());
            copyRes1.setScrapTime(scrap.getCreatedAt());
        }

        Collections.sort(resultCopyRes);
        return ResponseEntity.ok(new HttpRes<>(resultCopyRes));
    }



    // 카피라이팅 스크랩
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
    public ResponseEntity<HttpRes<String>> saveCrawling(@RequestBody CrawlingReq crawlingReq) {
        List<CrawlingData> dataList = crawlingReq.getData();

        copyService.saveCrawlingData(dataList);

        return ResponseEntity.ok(new HttpRes<>("크롤링 데이터가 저장되었습니다."));
    }


    // 카피라이팅 필터링
    @GetMapping("/filter/{index}")
    public ResponseEntity<List<CopyRes>> searchFilterCard(HttpServletRequest httpRequest, @PathVariable int index, CardSearchCondition condition) {
        List<CopyRes> resultCopyRes = copyService.searchFilterCard(httpRequest, index, condition);
        if (resultCopyRes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultCopyRes);
    }




    // 카피라이팅 스크랩 취소 (soft delete)
    @PostMapping ("/scrap/cancle")
    public ResponseEntity<HttpRes<String>> cancelScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq){
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Long cardId = copyReq.getCardId();
        Card card = cardRepository.findCardById(cardId);
        boolean is_canceled = copyService.cancelScrap(user, card);
        if(is_canceled){
            return ResponseEntity.ok(new HttpRes<>("해당 스크랩을 삭제했습니다."));
        }
        else{
            String errorMessage = "스크랩 정보가 유효하지 않습니다.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpRes<>(HttpStatus.BAD_REQUEST.value(), errorMessage));
        }

    }
}