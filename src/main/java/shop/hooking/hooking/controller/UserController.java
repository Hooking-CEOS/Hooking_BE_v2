package shop.hooking.hooking.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import shop.hooking.hooking.dto.response.OAuthUserRes;
import shop.hooking.hooking.dto.HttpRes;
import shop.hooking.hooking.dto.request.ReviewReq;
import shop.hooking.hooking.dto.response.ReviewRes;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.BadRequestException;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.service.JwtTokenProvider;
import shop.hooking.hooking.service.OAuthUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;

    private final OAuthUserService oAuthUserService;

    private final UserRepository userRepository;


    @GetMapping("/profile")
    public OAuthUserRes sessionMemberDetails(HttpServletRequest httpRequest) {
        OAuthUserRes oAuthUserRes = jwtTokenProvider.getKakaoInfo(httpRequest);
        return oAuthUserRes;
    }


//
//    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000, http://localhost:3001")
//    @PostMapping("/review")
//    public HttpRes<String> writeReview(@RequestBody ReviewReq.WriteReviewDto writeReviewDto,
//                                      HttpServletRequest httpServletRequest){
//        String token = jwtTokenProvider.resolveToken(httpServletRequest);
//        if(!jwtTokenProvider.validateToken(token,httpServletRequest)){
//            throw new BadRequestException("사용자 정보를 찾을 수 없습니다.");
//        }
//        User user = userRepository.findMemberByKakaoId(Long.parseLong(jwtTokenProvider.getUserPk(token)));
//        //Long senderId = user.getId();
//        String title = writeReviewDto.getTitle();
//        String content = writeReviewDto.getContent();
//        reviewService.writeReview(title, content, user);
//
//        return new HttpRes<>("건의사항이 정상적으로 처리되었습니다.");
//    }
//
//
//    @GetMapping("/review")
//    public List<ReviewRes.ReviewDto> getReviewList(HttpServletRequest httpServletRequest){
//        String token = jwtTokenProvider.resolveToken(httpServletRequest);
//        if(!jwtTokenProvider.validateToken(token,httpServletRequest)){
//            throw new BadRequestException("사용자 정보를 찾을 수 없습니다.");
//        }
//        User user = userRepository.findMemberByKakaoId(Long.parseLong(jwtTokenProvider.getUserPk(token)));
//        //Long senderId = user.getId();
//        List<ReviewRes.ReviewDto> reviewDtos = reviewService.getReviewList(user);
//        return reviewDtos;
//
//    }

//    @DeleteMapping("/user")
//    public String memberDelete(HttpServletRequest httpRequest) {
//        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
//        return oAuthUserService.deleteSessionMember(user.getId());
//    }

}