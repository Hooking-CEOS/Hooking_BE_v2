package shop.hooking.hooking.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import shop.hooking.hooking.dto.response.OAuthUserResponseDTO;
import shop.hooking.hooking.dto.review.ReviewReq;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.BadRequestException;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.service.JwtTokenProvider;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    // 유저 정보 반환
    @GetMapping("/profile")
    public OAuthUserResponseDTO sessionMemberDetails(HttpServletRequest httpRequest) {
        OAuthUserResponseDTO oAuthUserResponseDTO = jwtTokenProvider.getKakaoInfo(httpRequest);
        return oAuthUserResponseDTO;
    }

    // 건의사항 작성
    @PostMapping("/review")
    public void writeReview(@RequestBody ReviewReq.WriteReviewDto writeReviewDto,
                            HttpServletRequest httpServletRequest){ // <건의사항 내용>과 시간, <작성자 이름> 등등
        String token = jwtTokenProvider.resolveToken(httpServletRequest); //헤더에서 토큰을 빼내오는 과정
        if(!jwtTokenProvider.validateToken(token,httpServletRequest)){ //토큰이 유효하지 않을 때
            throw new BadRequestException("사용자 정보를 찾을 수 없습니다.");
        }
        User user = userRepository.findMemberByKakaoId(Long.parseLong(jwtTokenProvider.getUserPk(token)));
        Long senderId = user.getId(); //유저를 식별할 수 있는 컬럼 아무거나
        String content =writeReviewDto.getContent();
        // 리뷰서비스.리뷰쓰기(작성자아이디, 내용)
    }

}