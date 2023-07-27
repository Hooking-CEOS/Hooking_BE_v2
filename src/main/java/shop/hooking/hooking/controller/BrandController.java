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

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;


    // 전체 브랜드 기본정보 조회
    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @GetMapping("")
    public ResponseEntity<List<BrandRes.BrandDto>> showAllBrand(){
        List<BrandRes.BrandDto> brandDtoList = brandService.getBrandList();

        return ResponseEntity.status(HttpStatus.OK.value())
                .body(brandDtoList);
    }

    // 해당 브랜드 상세정보 조회

    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000/, http://localhost:3001/")
    @PostMapping("/{brand_id}/{index}")
    public ResponseEntity<BrandRes.BrandDetailDto> showOneBrand(HttpServletRequest httpRequest, @PathVariable Long brand_id, @PathVariable int index){
        BrandRes.BrandDetailDto brandDetailDto = brandService.getOneBrand(brand_id); // List<card> 가 전체 반환됨

        // 전체 card 리스트를 가져옴
        List<Card> cards = brandDetailDto.getCard();


        // index와 30을 곱하여 startIndex 계산
        int startIndex = index * 30;

        // startIndex부터 30개씩의 카드를 잘라서 resultCards 리스트에 저장
        List<Card> resultCards = getLimitedCardsByIndex(cards, startIndex);



        if (resultCards.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        brandDetailDto.setCard(resultCards);


         //로그인이 안되어있을 경우 scrapCnt를 0으로 설정
        if (jwtTokenProvider.getUserInfoByToken(httpRequest) == null) {
            setScrapCntWhenTokenNotProvided(brandDetailDto.getCard());
        }

        return ResponseEntity.status(HttpStatus.OK.value())
                .body(brandDetailDto);
    }


    private void setScrapCntWhenTokenNotProvided(List<Card> cardList) {
        for (Card card : cardList) {
            card.setScrapCnt(0);
        }
    }



    // startIndex부터 30개의 카드를 잘라서 반환하는 메서드
    private List<Card> getLimitedCardsByIndex(List<Card> cards, int startIndex) {
        int endIndex = Math.min(startIndex + 30, cards.size());
        return cards.subList(startIndex, endIndex);
    }

//    // 해당 브랜드 팔로우
//    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000, http://localhost:3001")
//    @PostMapping("/{brand_id}/follow")
//    public HttpRes<String> followBrand(@PathVariable Long brand_id, HttpServletRequest httpServletRequest)// 로그인한 사용자 정보 필요함
//    {
//        String token = jwtTokenProvider.resolveToken(httpServletRequest); //헤더에서 토큰을 빼내오는 과정
//        if(!jwtTokenProvider.validateToken(token,httpServletRequest)){ //토큰이 유효하지 않을 때
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
