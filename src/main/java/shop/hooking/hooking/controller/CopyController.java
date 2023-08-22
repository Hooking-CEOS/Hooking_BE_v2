package shop.hooking.hooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.request.CrawlingReq;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.CopySearchRes;
import shop.hooking.hooking.service.CopyService;
import springfox.documentation.annotations.Cacheable;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Cacheable("copyListCache")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/copy")
@Tag(name = "Copy")
public class CopyController {

    private final CopyService copyService;

    
    
    @Operation(summary = "전체 카피라이팅 조회하기")
    @GetMapping("/{index}")
    public ResponseEntity<List<CopyRes>> getCopyList(HttpServletRequest httpRequest, @PathVariable int index) {
        return ResponseEntity.ok(copyService.getCopyList(httpRequest,index));
    }


    @Operation(summary = "카피라이팅 검색하기")
    @GetMapping("/search/{index}")
    public ResponseEntity<CopySearchRes> searchCopyList(HttpServletRequest httpRequest,
                                                        @RequestParam(name = "keyword") String q,
                                                        @PathVariable int index) {
        return ResponseEntity.ok(copyService.searchCopyList(httpRequest, q, index));
    }


    @Operation(summary = "카피라이팅 스크랩 조회하기")
    @GetMapping("/scrap/{index}")
    public ResponseEntity<List<CopyRes>> getScrapList(HttpServletRequest httpRequest, @PathVariable int index) {
        return ResponseEntity.ok(copyService.getScrapList(httpRequest,index));
    }


    @Operation(summary = "카피라이팅 필터링")
    @GetMapping("/filter/{index}")
    public ResponseEntity<List<CopyRes>> getCopyFilter(HttpServletRequest httpRequest, @PathVariable int index, CardSearchCondition condition) {
        List<CopyRes> result = copyService.getCopyFilter(httpRequest, index, condition);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }


    @Operation(summary = "스크랩 하기")
    @PostMapping("/scrap")
    public ResponseEntity<?> createScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq) {
        return ResponseEntity.ok(copyService.createScrap(httpRequest,copyReq));
    }


    @Operation(summary = "스크랩 취소하기")
    @PostMapping ("/scrap/cancle")
    public ResponseEntity<?> deleteScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq){
        return ResponseEntity.ok(copyService.deleteScrap(httpRequest,copyReq));

    }


    @Operation(summary = "카피라이팅 크롤링")
    @PostMapping("/crawling")
    public ResponseEntity<?> createCrawling(@RequestBody CrawlingReq crawlingReq) {
        copyService.saveCrawlingData(crawlingReq);
        return ResponseEntity.ok("크롤링 데이터가 저장되었습니다.");
    }



}