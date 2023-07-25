package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/example")
public class RefererController {

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        // Referer 헤더 확인
        String referer = request.getHeader("Referer");
        System.out.println("Referer: " + referer);

        // Host 헤더 확인
        String host = request.getHeader("Host");
        System.out.println("Host: " + host);

        // 로그인 성공 후 리다이렉트할 URL 분기처리 등 추가 작업 수행

        return "Login success";
    }
}

