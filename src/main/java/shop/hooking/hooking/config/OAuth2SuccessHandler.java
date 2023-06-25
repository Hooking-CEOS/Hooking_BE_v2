package shop.hooking.hooking.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.service.JwtTokenProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//Success Handler에 진입했다는 것은, 로그인이 완료되었다는 뜻이다.
//        이 때가 정말 중요하다.
//
//        해당 클래스의 주요 기능은 크게 2가지이다.
//
//        최초 로그인인지 확인
//        Access Token, Refresh Token 생성 및 발급
//        token을 포함하여 리다이렉트
@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        User user = userRepository.findMemberByKakaoId(oAuth2User.getAttribute("id")); //해당 id를 디비에서 조회
        String role = user.getRole();
        Boolean firstLogin = oAuth2User.getAttribute("firstLogin");

        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);
        String targetUrl;
        log.info("토큰 발행 시작");

        String token = jwtTokenProvider.createJwtAccessToken(oAuth2User.getAttribute("id").toString(), role); //토큰발행
        log.info("{}", token);
        targetUrl = UriComponentsBuilder.fromUriString("/home") //토큰을 포함해 리다이렉트,우린 홈으로 설정
                .queryParam("token", token)
                .queryParam("firstLogin", firstLogin)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

