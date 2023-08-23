package shop.hooking.hooking.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.request.EmailGetDto;
import shop.hooking.hooking.dto.request.LoginFormDto;
import shop.hooking.hooking.dto.request.MemberFormDto;
import shop.hooking.hooking.dto.response.LoginInfoDto;
import shop.hooking.hooking.dto.response.LoginResDto;
import shop.hooking.hooking.exception.UserNotFoundException;
import shop.hooking.hooking.global.constant.ResponseConstant;
import shop.hooking.hooking.service.MemberService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RequestMapping(value = "/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    //@Operation(summary = "일반 회원가입하기")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody MemberFormDto memberFormDto) {
        memberService.signup(memberFormDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseConstant.SIGNUP_SUCCESS);
    }



    //@Operation(summary = "일반 로그인하기")
    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(@RequestBody LoginFormDto loginDto) {
        LoginInfoDto loginInfoDto = memberService.login(loginDto.getEmail(), loginDto.getPassword());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Set-Cookie",memberService.generateCookie("refreshToken", loginInfoDto.getRefreshToken()).toString());
        return new ResponseEntity<LoginResDto>(loginInfoDto.toLoginResDto(), responseHeaders, HttpStatus.OK);
    }





    //accesstoken만료 => 프론트 api
    //프론트: 바디-이메일, 헤더-쿠키를통한refreshtoken
    @PostMapping("/re-issue")
    public ResponseEntity<LoginResDto> reIssue(@RequestBody EmailGetDto reqDto, HttpServletRequest request, @CookieValue(value = "refreshToken", required = false) Cookie rCookie) {
        String refreshToken = rCookie.getValue();
        System.out.println("refreshToken = " + refreshToken);
        if(refreshToken == null)
        {
            throw new UserNotFoundException();//나중에 커스텀
        }
        LoginInfoDto responseDto = memberService.reIssueAccessToken(reqDto.getEmail(), refreshToken);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Set-Cookie",memberService.generateCookie("refreshToken", responseDto.getRefreshToken()).toString());
        return new ResponseEntity<LoginResDto>(responseDto.toLoginResDto(), responseHeaders, HttpStatus.OK);
    }

/*
    @GetMapping("/logout")
    public ResponseEntity<BasicResponse> logout(@RequestBody Member member, HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        memberService.logout(member.getEmail(), accessToken);
        BasicResponse response = new BasicResponse(HttpStatus.OK, ErrorCode.LOGOUT_SUCCESS, "LOGOUT_SUCCESS");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
*/
}