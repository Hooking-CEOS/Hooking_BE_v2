//package shop.hooking.hooking.global.config;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.CorsUtils;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import shop.hooking.hooking.config.CustomAuthenticationEntryPoint;
//import shop.hooking.hooking.global.config.jwt.JwtAuthenticationFilter;
//import shop.hooking.hooking.global.config.jwt.JwtProvider;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class AppConfig {
//
//    private final JwtProvider jwtProvider;
//
//    //인증 실패 또는 인증헤더가 전달받지 못했을때 핸들러
//    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
//
//
//    //비밀번호를 DB에 저장하기 전 사용할 암호화
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        //configuration.addAllowedOrigin("https://lakku-lakku.netlify.app");
//        configuration.addAllowedOrigin("http://localhost:3000");
//        configuration.addAllowedHeader("*");
//        configuration.addExposedHeader("Set-Cookie");
//        configuration.addAllowedMethod("*");
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors().configurationSource(corsConfigurationSource())
//                .and()
//                //기본설정은 비인증시 로그인폼으로 리다이렉트되는데, 그걸 방지함
//                .httpBasic().disable()
//                //rest api이므로 상태를 저장하지 않으니 csrf 보안을 설정하지 않아도 됨.
//                .csrf().disable()
//                //jwt로 인증하므로 세션이 필요하지 않으니 생성 안 함
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(customAuthenticationEntryPoint)
//                .and()
//                .authorizeRequests()
//                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
//                //웹 권한 관리 대상 지정
//                .antMatchers("/auth/signup/**", "/auth/login/**",
//                        "/auth/re-issue", "/auth/settings", "/auth/certification/**",
//                        "/api/v1/notification/subscribe", "/api/v1/util/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//}
