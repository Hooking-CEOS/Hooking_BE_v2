package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.ResDTO;
import shop.hooking.hooking.dto.SessionUserDTO;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.service.JwtTokenProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResDTO sessionMemberDetails(HttpServletRequest httpRequest) {
        ResDTO resDTO = jwtTokenProvider.getKakaoInfo(httpRequest);
        return resDTO;
    }


}