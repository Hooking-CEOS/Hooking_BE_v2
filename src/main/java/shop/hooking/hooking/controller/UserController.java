package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.ResDTO;
import shop.hooking.hooking.dto.SessionUserDTO;
import shop.hooking.hooking.service.JwtTokenProvider;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;

//    @GetMapping("/profile")
//    public SessionUserDTO sessionMemberDetails(HttpServletRequest httpRequest) {
//        SessionUserDTO sessionUser = jwtTokenProvider.getUserInfoByToken(httpRequest);
//        return sessionUser;
//    }

    @GetMapping("/profile")
    public ResDTO sessionMemberDetails(HttpServletRequest httpRequest) {
        ResDTO resDTO = jwtTokenProvider.getKakaoInfo(httpRequest);
        return resDTO;
    }


}