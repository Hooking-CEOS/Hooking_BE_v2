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

    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("")
    public ResponseEntity<HttpRes<List<BrandRes.BrandDto>>> showAllBrand() {
        List<BrandRes.BrandDto> brandDtoList = brandService.getBrandList();

        return ResponseEntity.ok(new HttpRes<>(brandDtoList));
    }

    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("/{brand_id}/{index}")
    public ResponseEntity<HttpRes<BrandRes.BrandDetailDto>> getOneBrand(HttpServletRequest httpRequest, @PathVariable Long brand_id, @PathVariable int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        BrandRes.BrandDetailDto brandDetailDto = brandService.getOneBrand(brand_id);


        List<BrandRes.cardDto> cards = brandDetailDto.getCard();

        int startIndex = index * 30;
        List<BrandRes.cardDto> resultCards = brandService.getLimitedCardsByIndex(cards, startIndex);


        if (resultCards.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpRes<>(HttpStatus.BAD_REQUEST.value(), "카드가 없습니다."));
        }

        brandDetailDto.setCard(resultCards);
        brandService.setScrapCntWhenTokenNotProvided(httpRequest, resultCards);
        brandService.setIsScrapWithUser(user, resultCards);

        return ResponseEntity.ok(new HttpRes<>(brandDetailDto));
    }

}
