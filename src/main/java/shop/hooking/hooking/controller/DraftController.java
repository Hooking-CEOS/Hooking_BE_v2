package shop.hooking.hooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.request.DraftReqDto;
import shop.hooking.hooking.dto.response.CategoryResDto;
import shop.hooking.hooking.service.DraftService;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/draft")
public class DraftController {
    private final DraftService draftService;

    @Operation(summary = "시안 생성하기")
    @PostMapping
    public ResponseEntity<?> createDraft(HttpServletRequest httpRequest, @RequestBody DraftReqDto draftReqDto) {
        //서비스 로직 추가
        draftService.createDraft(httpRequest, draftReqDto);
        return ResponseEntity.ok().build();

    }

    @Operation(summary = "시안 리스트 보여주기")
    @GetMapping
    public ResponseEntity<CategoryResDto> getDraftList(HttpServletRequest httpRequest,@PathVariable int index) {
        return ResponseEntity.ok(draftService.getDraftList(httpRequest,index)); //
    }

    @Operation(summary = "시안 검색하기")
    @GetMapping("/search")
    public ResponseEntity<CategoryResDto> searchDraftList(HttpServletRequest httpRequest, @RequestParam(name = "keyword") String q, @PathVariable int index) {
        return ResponseEntity.ok(draftService.searchDraftList(httpRequest,q, index)); //
    }
}
