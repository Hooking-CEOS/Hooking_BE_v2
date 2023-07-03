package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.response.BrandRes;
import shop.hooking.hooking.dto.response.ReviewRes;
import shop.hooking.hooking.service.BrandService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;

    // 전체 브랜드 기본정보 조회
    @GetMapping("")
    public List<BrandRes.BrandDto> showAllBrand(){ // 로그인하지 않은 모든 사용자가 이용할 수 있음
        List<BrandRes.BrandDto> brandDtoList = brandService.getBrandList();

        return brandDtoList; // 브랜드 기본정보 반환
    }

    // 해당 브랜드 상세정보 조회
    @PostMapping("/{brand_id}")
    public BrandRes.BrandDetailDto showOneBrand(@PathVariable Long brand_id){ // 로그인하지 않은 모든 사용자가 이용할 수 있음
        BrandRes.BrandDetailDto brandDetailDto = brandService.getOneBrand(brand_id);

        return brandDetailDto; // 브랜드 상세정보 반환
    }
}
