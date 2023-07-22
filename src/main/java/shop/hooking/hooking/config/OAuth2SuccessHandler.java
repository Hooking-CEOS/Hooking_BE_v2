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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal(); // 카카오로부터 받은 유저 정보
        User user = userRepository.findMemberByKakaoId(oAuth2User.getAttribute("id")); // 해당 id를 디비에서 조회
        String role = user.getRole();
        Boolean firstLogin = oAuth2User.getAttribute("firstLogin");

        log.info("OAuth2User = {}", oAuth2User);
        String targetUrl;
        log.info("토큰 발행 시작");

        String token = jwtTokenProvider.createJwtAccessToken(oAuth2User.getAttribute("id").toString(), role); //토큰발행
        log.info("{}", token);
        //배포url, 로컬url -> 커스텀파라미터
        targetUrl = UriComponentsBuilder.fromUriString("https://hooking-dev.netlify.app/")
                .queryParam("token", token)
                .queryParam("firstLogin", firstLogin)
                .build().toUriString();
        //response body로 줌
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

}

