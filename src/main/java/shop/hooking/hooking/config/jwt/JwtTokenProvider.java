package shop.hooking.hooking.config.jwt;



import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.RefreshTokenExpiredException;
import shop.hooking.hooking.exception.UserNotFoundException;
import shop.hooking.hooking.global.redis.RedisService;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.dto.response.OAuthUserRes;
import shop.hooking.hooking.service.CustomUsersDetailsService;
//import shop.hooking.hooking.service.CustomUsersDetailsService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;


//토큰 생성

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {


    private final UserRepository userRepository;
    private final CustomUsersDetailsService customUsersDetailsService;
    private final RedisService redisService;

    @Value("${spring.jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${spring.jwt.blacklist.access-token}")
    private String blackListATPrefix;

    private final long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 120; // 2h

    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    // JWT token 생성
    public String createOauthAccessToken(String userPk, String roles) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);  // 권한 설정, key/ value 쌍으로 저장
        Date now = new Date(); // 현재 시간 -> 유효기간 확인을 위함
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public String createAccessToken(String userId, String roles) {
        Long tokenInvalidTime = 1000L * 60 * 120; // 2h
        return this.createToken(userId, roles, tokenInvalidTime);
    }


    public String createRefreshToken(String userId, String roles) {
        Long tokenInvalidTime = 1000L * 60 * 60 * 24; // 1d
        String refreshToken = this.createToken(userId, roles, tokenInvalidTime);
        //refresh token은 redis에 저장
        redisService.setValues(userId, refreshToken, Duration.ofMillis(tokenInvalidTime));
        return refreshToken;
    }

    public String createToken(String userId, String roles, Long tokenInvalidTime) {
        //user 구분을 위해 user pk 값 넣어줌
        Claims claims = Jwts.claims().setSubject(userId); // claims 생성 및 payload 설정
        claims.put("roles", roles); // 권한 설정, key/ value 쌍으로 저장
        Date date = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public Authentication validateToken(HttpServletRequest request, String token) {
        String exception = "exception";
        try {
            String expiredAT = redisService.getValues(blackListATPrefix + token);
            if (expiredAT != null) {
                throw new ExpiredJwtException(null, null, null);
            }
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
        if (userDetailsExists(token)) { //일반로그인
            UserDetails userDetails = customUsersDetailsService.loadUserByUsername(getUserPk(token));
            System.out.println(userDetails+"getAuthentication");
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } else { //소셜로그인
            User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)))
                    .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
            OAuthUserRes resDTO = OAuthUserRes.builder().user(user).build();
            return new UsernamePasswordAuthenticationToken(resDTO, "", resDTO.getAuthorities());
        }
    }

    private boolean userDetailsExists(String token) {
        try {
            UserDetails userDetails = customUsersDetailsService.loadUserByUsername(getUserPk(token));
            return userDetails != null;
        } catch (UsernameNotFoundException ex) {
            return false;
        }
    }



    // jwt에서 회원정보 추출
    public String getUserPk(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }
    private String getUserEmail(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    //HTTP 요청 안에서 헤더 찾아서 토큰 가져옴
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }


    public User getUserInfoByToken(HttpServletRequest request) {
        String token = resolveToken(request);
        Authentication authentication = validateToken(request, token);
        String email = getUserEmail(token);
        if (authentication != null) {
            if (userDetailsExists(token)){
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
                return user;
            }
            else {
                User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)))
                        .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
                return user;
            }
        }
        else {
            return null;
        }
    }



    private Long getUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return null;
    }



    public void checkRefreshToken(String userId, String refreshToken) {
        String redisRT = redisService.getValues(userId);
        if (!refreshToken.equals(redisRT)) {
            throw new RefreshTokenExpiredException();
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