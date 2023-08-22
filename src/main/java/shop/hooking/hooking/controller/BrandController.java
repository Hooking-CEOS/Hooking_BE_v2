package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.HttpRes;
import shop.hooking.hooking.dto.response.BrandRes;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.service.BrandService;
import shop.hooking.hooking.service.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brand")
public class BrandController {

    private final BrandService brandService;

    private final JwtTokenProvider jwtTokenProvider;

    //브랜드 전체 조회
    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("")
    public ResponseEntity<HttpRes<List<BrandRes.BrandDto>>> showAllBrand() {
        return ResponseEntity.ok(new HttpRes<>(brandService.getBrandList()));
    }

    //브랜드 상세 조회
    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("/{brand_id}/{index}")
    public ResponseEntity<HttpRes<BrandRes.BrandDetailDto>> getOneBrand(HttpServletRequest httpRequest, @PathVariable Long brand_id, @PathVariable int index) {
        return ResponseEntity.ok(new HttpRes<>(brandService.getBrandDetail(httpRequest, brand_id, index)));
    }

}
