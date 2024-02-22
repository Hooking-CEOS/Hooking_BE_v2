package shop.hooking.hooking.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.request.CopyReqDto;
import shop.hooking.hooking.dto.request.CrawlingReqDto;

import shop.hooking.hooking.dto.request.ScrapReqDto;
import shop.hooking.hooking.dto.request.RandomSeedDto;
import shop.hooking.hooking.dto.response.CopyResDto;
import shop.hooking.hooking.dto.response.CopySearchResDto;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.Contain;
import shop.hooking.hooking.entity.Scrap;
import shop.hooking.hooking.exception.OutOfIndexException;
import shop.hooking.hooking.repository.ContainRepository;
import shop.hooking.hooking.service.CopyService;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/copy")
public class CopyController {

    private final CopyService copyService;
    private final ContainRepository containRepository;


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



    @Operation(summary = "브랜드 카피라이팅 검색하기")
    @PostMapping("/search/brand/{index}")
    public ResponseEntity<CopySearchResDto> searchBrandList(HttpServletRequest httpRequest,
                                                            @RequestParam(name = "keyword") String q,
                                                            @PathVariable int index, @RequestBody(required = false) RandomSeedDto randomSeedDto) {
        return ResponseEntity.ok(copyService.searchBrandList(httpRequest, q, index, randomSeedDto.getRandomSeed()));
    }

    @Operation(summary = "키워드 카피라이팅 검색하기")
    @PostMapping("/search/text/{index}")
    public ResponseEntity<CopySearchResDto> searchCopyList(HttpServletRequest httpRequest,
                                                           @RequestParam(name = "keyword") String q,
                                                           @PathVariable int index,@RequestBody(required = false) RandomSeedDto randomSeedDto) {
        return ResponseEntity.ok(copyService.searchCopyList(httpRequest, q, index,randomSeedDto.getRandomSeed()));
    }

    @Operation(summary = "무드 카피라이팅 검색하기")
    @PostMapping("/search/mood/{index}")
    public ResponseEntity<CopySearchResDto> searchMoodList(HttpServletRequest httpRequest,
                                                           @RequestParam(name = "keyword") String q,
                                                           @PathVariable int index,@RequestBody(required = false) RandomSeedDto randomSeedDto) {
        return ResponseEntity.ok(copyService.searchMoodList(httpRequest, q, index, randomSeedDto.getRandomSeed()));
    }

    @Operation(summary = "카피라이팅 스크랩 조회하기")
    @GetMapping("/scrap/{index}")
    public ResponseEntity<List<CopyResDto>> getScrapList(HttpServletRequest httpRequest, @PathVariable int index) {
        return ResponseEntity.ok(copyService.getScrapList(httpRequest,index));
    }


    @Operation(summary = "카피라이팅 필터링")
    @GetMapping("/filter/{index}")
    public ResponseEntity<List<CopyResDto>> getCopyFilter(HttpServletRequest httpRequest, @PathVariable int index, CardSearchCondition condition) {
        List<CopyResDto> result = copyService.getCopyFilter(httpRequest, index, condition);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }


    @Operation(summary= "사용자의 폴더 리스트 보여주기")
    @GetMapping("/folder")
    public ResponseEntity<List<String>> getFolderList(HttpServletRequest httpRequest){
        return ResponseEntity.ok(copyService.getFolderList(httpRequest));

    }

    @Operation(summary = "폴더 스크랩 세부 조회하기")
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<CopyResDto>> getFolderScrap(HttpServletRequest httpRequest, @PathVariable Long folderId) {
        // Contain 엔티티에서 폴더에 속한 스크랩 조회
        List<Contain> contains = containRepository.findByFolderId(folderId);
        List<CopyResDto> copyResDtos = new ArrayList<>();

        // 조회된 스크랩이 없는 경우
        if (contains.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>()); // 빈 목록 반환
        }

        for (Contain contain : contains) {
            Scrap scrap = contain.getScrap();
            Card card = scrap.getCard(); // 스크랩에서 카드 정보 가져오기

            // CopyResDto 객체 생성
            CopyResDto copyResDto = CopyResDto.builder()
                    .id(card.getId())
                    .brand(scrap.getCard().getBrand())
                    .text(card.getText())
                    .createdAt(scrap.getCreatedAt())
                    .cardLink(scrap.getCard().getUrl())
                    .build();

            copyResDtos.add(copyResDto);
        }

        // 스크랩된 CopyResDto 목록을 반환
        return ResponseEntity.ok(copyResDtos);
    }


    @Operation(summary = "스크랩 하기")
    @PostMapping("/scrap")
    public ResponseEntity<?> createScrap(HttpServletRequest httpRequest, @RequestBody ScrapReqDto scrapReqDto) {
        copyService.createScrap(httpRequest, scrapReqDto);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "스크랩 취소하기")
    @PostMapping ("/scrap/cancle")
    public ResponseEntity<?> deleteScrap(HttpServletRequest httpRequest, @RequestBody CopyReqDto copyReqDto){
        return ResponseEntity.ok(copyService.deleteScrap(httpRequest,copyReqDto));

    }


    @Operation(summary = "카피라이팅 크롤링")
    @PostMapping("/crawling")
    public ResponseEntity<?> createCrawling(@RequestBody CrawlingReqDto crawlingReqDto) {
        copyService.saveCrawlingData(crawlingReqDto);
        return ResponseEntity.ok("크롤링 데이터가 저장되었습니다.");
    }



}