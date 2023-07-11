package shop.hooking.hooking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.hooking.hooking.service.JwtTokenProvider;
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("https://hooking.shop/","https://hooking-dev.netlify.app/","https://hooking.netlify.app/","http://localhost:3000/","http://localhost:3001/"));
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
            .authorizeRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()


            .antMatchers("**/oauth2/**","/kakaologin","/copy", "/copy/search","/copy/filter","copy/crawling", "/brand", "/brand/{brand_id}"
                    ).permitAll()

            .anyRequest().authenticated()
            .and()
                .logout()
                .and()
                .oauth2Login()
                .successHandler(authenticationSuccessHandler) //전체로그인 성공 시, handler를 설정
                .userInfoEndpoint() //OAuth 2 로그인 성공 이후 사용자 정보를 가져올 때의 설정
                .userService(oAuthUserService); //소설 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록

    return http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class).build();
}
}

