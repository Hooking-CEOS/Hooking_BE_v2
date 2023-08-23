package shop.hooking.hooking.config.jwt;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.hooking.hooking.config.jwt.JwtTokenProvider;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//JWT 인증
@RequiredArgsConstructor
class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request.getHeader("Authorization"));// HTTP header에서 token 받아오기
        System.out.println(token);

        if (token != null) {
            Authentication authentication = jwtTokenProvider.validateToken(request, token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("token received");
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
        }
        filterChain.doFilter(request, response); // 필터 작동
    }

    private String resolveToken(String authorization) {
        return authorization != null ? authorization.substring(7) : null;
    }
}

