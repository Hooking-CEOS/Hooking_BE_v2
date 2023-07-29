package shop.hooking.hooking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.service.JwtTokenProvider;
import shop.hooking.hooking.service.OAuthUserService;
import org.springframework.beans.factory.annotation.Value;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//로그인 다 완료되고 우리 서버에서 쓸 수 있는 jwt 토큰을 발급
@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private OAuthUserService oAuthUserService;

    @Value("${app.deployment.url}")
    private String requestUrl;

    @Value("${app.deployment.processor.url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 카카오로부터 받은 유저 정보
        User user = userRepository.findMemberByKakaoId(oAuth2User.getAttribute("id")); // 해당 id를 디비에서 조회
        String role = user.getRole();
        Boolean firstLogin = oAuth2User.getAttribute("firstLogin");

        log.info("OAuth2User = {}", oAuth2User);
        String targetUrl;
        log.info("토큰 발행 시작");
        String token = jwtTokenProvider.createJwtAccessToken(oAuth2User.getAttribute("id").toString(), role); //토큰발행
        log.info("{}", token);

        String referer = request.getHeader("Referer");
        String host = request.getHeader("Host");

        // Referer와 Host에 따라서 targetUrl 설정
        if (referer != null && referer.startsWith(requestUrl) && host.equals("hooking.shop")) {
            targetUrl = redirectUrl; // 배포 환경
        } else if (referer != null && referer.startsWith("http://localhost:3000/") && host.equals("hooking.shop")) {
            targetUrl = "http://localhost:3000/oath-processor"; // 로컬 환경
        } else if (referer != null && referer.startsWith("https://hooking.me/") && host.equals("hooking.shop")) {
            targetUrl = "https://hooking.me/oath-processor"; // 실배포 환경
        }
        else {
            // 기본적으로 로컬 개발 환경으로 설정
            targetUrl = redirectUrl; // 로컬 환경
        }

        // 쿼리 파라미터를 추가하여 targetUrl 생성
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam("firstLogin", firstLogin)
                .build().toUriString();

        // response body로 보냄
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}


