package shop.hooking.hooking.global.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.service.RedisService;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.dto.response.OAuthUserResDto;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;


//토큰 생성
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {


    private final UserRepository userRepository;
    private final RedisService redisService;

    @Value("${spring.jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${spring.jwt.blacklist.access-token}")
    private String blackListATPrefix;


    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }


    // JWT token 생성
    public String createAccessToken(String userPk, String roles) {
        Long tokenInvalidTime = 1000L * 60 * 120; // 2h
        String accessToken = this.createToken(userPk, roles, tokenInvalidTime);
        return accessToken;
    }



    public String createRefreshToken(String userPK, String roles) {
        Long tokenInvalidTime = 1000L * 60 * 60 * 24; // 1d
        String refreshToken = this.createToken(userPK, roles, tokenInvalidTime);
        //refresh token은 redis에 저장
        redisService.setValues(userPK, refreshToken, Duration.ofMillis(tokenInvalidTime));
        return refreshToken;
    }

    public String createToken(String userPK, String roles, Long tokenInvalidTime) {
        //user 구분을 위해 user pk 값 넣어줌
        Claims claims = Jwts.claims().setSubject(userPK); // claims 생성 및 payload 설정
        claims.put("roles", roles); // 권한 설정, key/ value 쌍으로 저장
        Date date = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + tokenInvalidTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }




    public Authentication validateToken(HttpServletRequest request, String token) {
        String exception = "exception";
        try {
//            String expiredAT = redisService.getValues(blackListATPrefix + token);
//            if (expiredAT != null) {
//                throw new ExpiredJwtException(null, null, null);
//            }
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return getAuthentication(token);
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            request.setAttribute(exception, "토큰의 형식을 확인하세요.");
        } catch (ExpiredJwtException e) {
            request.setAttribute(exception, "access 토큰이 만료되었습니다.");
        } catch (IllegalArgumentException e) {
            request.setAttribute(exception, "JWT compact of handler are invalid");
        }
        return null;
    }



    public Authentication getAuthentication(String token) {
        User user = userRepository.findUserByKakaoId(Long.parseLong(getUserPk(token)))
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
        OAuthUserResDto resDTO = OAuthUserResDto.builder().user(user).build();
        return new UsernamePasswordAuthenticationToken(resDTO, "", resDTO.getAuthorities());

    }




    // jwt에서 회원정보 추출
    public String getUserPk(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    //HTTP 요청 안에서 헤더 찾아서 토큰 가져옴
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }


    public User getUserInfoByToken(HttpServletRequest request) {
        String token = resolveToken(request);
        Authentication authentication = validateToken(request, token);
        if (authentication != null) {
            User user = userRepository.findUserByKakaoId(Long.parseLong(getUserPk(token)))
                    .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
            return user;
        }
        else {
            return null;
        }
    }





    public void checkRefreshToken(String userId, String refreshToken) {
        String redisRT = redisService.getValues(userId);
        if (!refreshToken.equals(redisRT)) {
            //throw new RefreshTokenExpiredException();
        }
    }

    // access 토큰 만료시간을 체크 후, redis에 (blacklist) + accessToken, 계정, 만료기간을이 담긴
    // ValueOperation을 만들어 redis에 저장한다.
    // redis에서 유저 refreshtoken 값을 삭제한다
    public void logout(String userId, String accessToken) {
        Date expiration = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(accessToken).getBody().getExpiration();
        long expiredAccessTokenTime = expiration.getTime() - new Date().getTime();
        redisService.setValues(blackListATPrefix + accessToken, userId, Duration.ofMillis(expiredAccessTokenTime));
        redisService.deleteValues(userId);
    }
}
