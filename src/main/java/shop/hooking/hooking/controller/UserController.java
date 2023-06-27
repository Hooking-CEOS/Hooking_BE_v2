package shop.hooking.hooking.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import shop.hooking.hooking.dto.response.OAuthUserResponseDTO;
import shop.hooking.hooking.service.JwtTokenProvider;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;

    // 유저 정보 반환
    @GetMapping("/profile")
    public OAuthUserResponseDTO sessionMemberDetails(HttpServletRequest httpRequest) {
        OAuthUserResponseDTO oAuthUserResponseDTO = jwtTokenProvider.getKakaoInfo(httpRequest);
        return oAuthUserResponseDTO;
    }


}