package shop.hooking.hooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.response.BrandResDto;
import shop.hooking.hooking.service.BrandService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/brand")
public class BrandController {

    private final BrandService brandService;



    //브랜드 전체 조회
    @Operation(summary = "브랜드 전체 조회하기")
    @GetMapping("")
    public ResponseEntity<List<BrandResDto.BrandDto>> showAllBrand() {
        return ResponseEntity.ok(brandService.getBrandList());

    }


    //브랜드 상세 조회 - 수정
    @Operation(summary = "브랜드 상세 조회하기")
    @GetMapping("/{brand_id}/{index}")
    public ResponseEntity<BrandResDto.BrandDetailDto> getOneBrand(HttpServletRequest httpRequest, @PathVariable Long brand_id, @PathVariable int index) {
        return ResponseEntity.ok(brandService.getBrandDetail(httpRequest, brand_id, index));
    }


}
