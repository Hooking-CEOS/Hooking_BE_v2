package shop.hooking.hooking.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.request.CopyReqDto;
import shop.hooking.hooking.dto.request.CrawlingReqDto;
import shop.hooking.hooking.dto.response.CopyResDto;
import shop.hooking.hooking.dto.response.CopySearchResDto;
import shop.hooking.hooking.exception.OutOfIndexException;
import shop.hooking.hooking.service.CopyService;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/copy")
//@Tag(name = "Copy")
public class CopyController {

    private final CopyService copyService;


    @Operation(summary = "전체 카피라이팅 조회하기")
    @GetMapping("/{index}")
    public ResponseEntity<List<CopyResDto>> getCopyList(HttpServletRequest httpRequest, @PathVariable int index) {
        try {
            List<CopyResDto> copyResList = copyService.getCopyList(httpRequest, index);
            return ResponseEntity.ok(copyResList);
        } catch (IllegalArgumentException ex) {
            throw new OutOfIndexException();
        }
    }


    //@Operation(summary = "브랜드 카피라이팅 검색하기")
    @GetMapping("/search/brand/{index}")
    public ResponseEntity<CopySearchResDto> searchBrandList(HttpServletRequest httpRequest,
                                                            @RequestParam(name = "keyword") String q,
                                                            @PathVariable int index) {
        return ResponseEntity.ok(copyService.searchBrandList(httpRequest, q, index));
    }

    //@Operation(summary = "키워드 카피라이팅 검색하기")
    @GetMapping("/search/text/{index}")
    public ResponseEntity<CopySearchResDto> searchCopyList(HttpServletRequest httpRequest,
                                                           @RequestParam(name = "keyword") String q,
                                                           @PathVariable int index) {
        return ResponseEntity.ok(copyService.searchCopyList(httpRequest, q, index));
    }

    //@Operation(summary = "무드 카피라이팅 검색하기")
    @GetMapping("/search/mood/{index}")
    public ResponseEntity<CopySearchResDto> searchMoodList(HttpServletRequest httpRequest,
                                                           @RequestParam(name = "keyword") String q,
                                                           @PathVariable int index) {
        return ResponseEntity.ok(copyService.searchMoodList(httpRequest, q, index));
    }


    //@Operation(summary = "카피라이팅 스크랩 조회하기")
    @GetMapping("/scrap/{index}")
    public ResponseEntity<List<CopyResDto>> getScrapList(HttpServletRequest httpRequest, @PathVariable int index) {
        return ResponseEntity.ok(copyService.getScrapList(httpRequest,index));
    }


    //@Operation(summary = "카피라이팅 필터링")
    @GetMapping("/filter/{index}")
    public ResponseEntity<List<CopyResDto>> getCopyFilter(HttpServletRequest httpRequest, @PathVariable int index, CardSearchCondition condition) {
        List<CopyResDto> result = copyService.getCopyFilter(httpRequest, index, condition);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }


    //@Operation(summary = "스크랩 하기")
    @PostMapping("/scrap")
    public ResponseEntity<?> createScrap(HttpServletRequest httpRequest, @RequestBody CopyReqDto copyReqDto) {
        return ResponseEntity.ok(copyService.createScrap(httpRequest,copyReqDto));
    }


    //@Operation(summary = "스크랩 취소하기")
    @PostMapping ("/scrap/cancle")
    public ResponseEntity<?> deleteScrap(HttpServletRequest httpRequest, @RequestBody CopyReqDto copyReqDto){
        return ResponseEntity.ok(copyService.deleteScrap(httpRequest,copyReqDto));

    }


    //@Operation(summary = "카피라이팅 크롤링")
    @PostMapping("/crawling")
    public ResponseEntity<?> createCrawling(@RequestBody CrawlingReqDto crawlingReqDto) {
        copyService.saveCrawlingData(crawlingReqDto);
        return ResponseEntity.ok("크롤링 데이터가 저장되었습니다.");
    }



}