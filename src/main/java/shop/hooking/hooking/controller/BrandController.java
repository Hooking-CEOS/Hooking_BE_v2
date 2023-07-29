package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.HttpRes;
import shop.hooking.hooking.dto.response.BrandRes;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.ReviewRes;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.BadRequestException;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.service.BrandService;
import shop.hooking.hooking.service.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;

    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("")
    public ResponseEntity<HttpRes<List<BrandRes.BrandDto>>> getAllBrand(){
        List<BrandRes.BrandDto> brandDtoList = brandService.getBrandList();

        return ResponseEntity.ok(new HttpRes<>(brandDtoList));
    }

    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("/{brand_id}/{index}")
    public ResponseEntity<HttpRes<BrandRes.BrandDetailDto>> getOneBrand(HttpServletRequest httpRequest, @PathVariable Long brand_id, @PathVariable int index){
        BrandRes.BrandDetailDto brandDetailDto = brandService.getOneBrand(brand_id);


        List<BrandRes.cardDto> cards = brandDetailDto.getCard();

        int startIndex = index * 30;
        List<BrandRes.cardDto> resultCards = brandService.getLimitedCardsByIndex(cards, startIndex);


        if (resultCards.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpRes<>(HttpStatus.BAD_REQUEST.value(), "카드가 없습니다."));
        }

        brandDetailDto.setCard(resultCards);
        brandService.setScrapCntWhenTokenNotProvided(httpRequest, resultCards);

        return ResponseEntity.ok(new HttpRes<>(brandDetailDto));
    }


//    // 해당 브랜드 팔로우
//    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000, http://localhost:3001")
//    @PostMapping("/{brand_id}/follow")
//    public HttpRes<String> followBrand(@PathVariable Long brand_id, HttpServletRequest httpServletRequest)
//    {
//        String token = jwtTokenProvider.resolveToken(httpServletRequest);
//        if(!jwtTokenProvider.validateToken(token,httpServletRequest)){
//            throw new BadRequestException("사용자 정보를 찾을 수 없습니다.");
//        }
//        User user = userRepository.findMemberByKakaoId(Long.parseLong(jwtTokenProvider.getUserPk(token)));
//
//        boolean isFollow = brandService.followBrand(brand_id, user);
//        if(isFollow){
//            return new HttpRes<>("해당 브랜드 팔로우가 완료되었습니다.");
//        }
//        return new HttpRes<>("이미 팔로우하였습니다. ");
//    }
}
