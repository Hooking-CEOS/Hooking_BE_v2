package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.HttpRes;
import shop.hooking.hooking.dto.response.BrandRes;
import shop.hooking.hooking.dto.response.ReviewRes;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.BadRequestException;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.service.BrandService;
import shop.hooking.hooking.service.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;
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

    // 해당 브랜드 팔로우하기
    @PostMapping("/{brand_id}/follow")
    public HttpRes<String> followBrand(@PathVariable Long brand_id, HttpServletRequest httpServletRequest)// 로그인한 사용자 정보 필요함
    {
        String token = jwtTokenProvider.resolveToken(httpServletRequest); //헤더에서 토큰을 빼내오는 과정
        if(!jwtTokenProvider.validateToken(token,httpServletRequest)){ //토큰이 유효하지 않을 때
            throw new BadRequestException("사용자 정보를 찾을 수 없습니다.");
        }
        User user = userRepository.findMemberByKakaoId(Long.parseLong(jwtTokenProvider.getUserPk(token)));

        brandService.followBrand(brand_id, user); // 여기서 브랜드 팔로우 기능을 구현해야함

        return new HttpRes<>("해당 브랜드 팔로우가 완료되었습니다.");
    }
}
