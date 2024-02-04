package shop.hooking.hooking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.hooking.hooking.entity.Role;
import shop.hooking.hooking.global.jwt.CustomAuthenticationEntryPoint;
import shop.hooking.hooking.global.jwt.JwtAuthenticationFilter;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;
import shop.hooking.hooking.service.OAuthUserService;

import java.util.Arrays;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)

public class SecurityConfig {
    private final OAuthUserService oAuthUserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    //인증 실패 또는 인증헤더가 전달받지 못했을때 핸들러
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("https://hooking.me/", "https://hooking.shop/", "https://hooking-dev.netlify.app/", "https://hooking.netlify.app/", "http://localhost:3000/", "http://localhost:3001/"));
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("Set-Cookie");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/v2/**", "/", "/css/**", "/images/**", "/js/**", "/h2/**",
                        "/select-role","/information", "/file/**",
                        /* swagger v3 */
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                ).permitAll()
//                .antMatchers("/api/v2/**").hasRole(Role.
//                        USER.name())
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()//인가코드 > 토큰받아옴 > 유저정보 요청(여기까지 자동으로 loadUser해줌) > 유저정보 받아옴
                .successHandler(authenticationSuccessHandler) //전체로그인 성공 시, handler를 설정
                .userInfoEndpoint() //OAuth 2 로그인 성공 이후 사용자 정보를 가져올 때의 설정
                .userService(oAuthUserService); //소설 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록

        return http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class).build();
    }
}
