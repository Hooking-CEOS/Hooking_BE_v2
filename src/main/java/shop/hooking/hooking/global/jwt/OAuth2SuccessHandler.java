package shop.hooking.hooking.global.jwt;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import shop.hooking.hooking.service.OAuthUserService;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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
        User user = userRepository.findUserByKakaoId(oAuth2User.getAttribute("id"))
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
        String role = String.valueOf(user.getRole());
        log.info("OAuth2User: " + oAuth2User);
        String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getAttribute("id").toString(), role); //토큰발행
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuth2User.getAttribute("id").toString(), role); //토큰발행
        log.info("accessToken: " + accessToken);
        log.info("refreshToken: " + refreshToken);

        String referer = request.getHeader("Referer");
        String host = request.getHeader("Host");
        String targetUrl;
        targetUrl = referer;
        log.info(referer);

//        // Referer와 Host에 따라서 targetUrl 설정
//        if (referer != null && referer.startsWith(requestUrl) && host.equals("hooking.shop")) {
//            targetUrl = redirectUrl; // 배포 환경
//        } else if (referer != null && referer.startsWith("http://localhost:3000/") && host.equals("hooking.shop")) {
//            targetUrl = "http://localhost:3000/oath-processor"; // 로컬 환경
//        } else if (referer != null && referer.startsWith("https://hooking.me/") && host.equals("hooking.shop")) {
//            targetUrl = "https://hooking.me/oath-processor"; // 실배포 환경
//        }
//        else {
//            // 기본적으로 로컬 개발 환경으로 설정
//            targetUrl ="https://hooking.me/oath-processor"; // 로컬 환경
//        }

        writeTokenResponse(request, response, accessToken, refreshToken, targetUrl);
    }

    private void writeTokenResponse(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken, String targetUrl) throws IOException {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setHttpOnly(false);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

//        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
//                .build().toUriString();
        response.sendRedirect(targetUrl);
        log.info("타켓URl, 쿠키정보: " + targetUrl, accessTokenCookie, refreshTokenCookie);
        // getRedirectStrategy().sendRedirect(request,response, targetUrl);
    }

}


