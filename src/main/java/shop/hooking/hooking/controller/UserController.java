package shop.hooking.hooking.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import shop.hooking.hooking.dto.response.OAuthUserRes;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.service.JwtTokenProvider;
import shop.hooking.hooking.service.OAuthUserService;

import javax.servlet.http.HttpServletRequest;


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

}