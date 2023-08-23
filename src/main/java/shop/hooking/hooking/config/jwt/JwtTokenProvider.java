package shop.hooking.hooking.config.jwt;



import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.RefreshTokenExpiredException;
import shop.hooking.hooking.global.redis.RedisService;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.dto.response.OAuthUserRes;
import shop.hooking.hooking.service.CustomUsersDetailsService;

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
    public String createJwtAccessToken(String userPk, String roles) {
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

    public String createRefreshToken(String userId, String roles) {
        Long tokenInvalidTime = 1000L * 60 * 60 * 24; // 1d
        String refreshToken = this.createToken(userId, roles, tokenInvalidTime);
        //refresh token은 redis에 저장
        redisService.setValues(userId, refreshToken, Duration.ofMillis(tokenInvalidTime));
        return refreshToken;
    }


    public Authentication getAuthentication(String token) {
        User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)));
        OAuthUserRes resDTO = OAuthUserRes.builder().user(user).build();
        return new UsernamePasswordAuthenticationToken(resDTO, "", resDTO.getAuthorities());
    }

    private Authentication getAuthentication2(String token) {
        UserDetails userDetails = customUsersDetailsService.loadUserByUsername(getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    // jwt에서 회원정보 추출
    public String getUserPk(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    // HTTP 요청 안에서 헤더 찾아서 토큰 가져옴
    public String resolveToken(HttpServletRequest request){
            return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken, HttpServletRequest request) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date()); // 유효하면 return
        } catch (SignatureException e){
            System.out.println("Invalid Signature");
            return false; //유효하지 않은 경우
        } catch (MalformedJwtException e){
            System.out.println("Invalid JWT");
            return false; //유효하지 않은 경우
        } catch (ExpiredJwtException e){
            System.out.println("Expired JWT");
            return false; //유효하지 않은 경우
        } catch (UnsupportedJwtException e){
            System.out.println("Unsupported Excepton");
            return false; //유효하지 않은 경우
        } catch (IllegalArgumentException e){
            System.out.println("Empty JWT Claims string");
            return false; //유효하지 않은 경우
        }

    }

    public Authentication validateToken2(HttpServletRequest request, String token) {
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



    public OAuthUserRes getKakaoInfo(HttpServletRequest request) {
        String token = resolveToken(request);
        if(validateToken(token,request)) {
            User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)));
            return new OAuthUserRes(user);
        }
        else {
            return null;
        }
    }


    public User getUserInfoByToken(HttpServletRequest request) {
        String token = resolveToken(request);
        if(validateToken(token,request)) {
            User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)));
            return user;
        }
        else {
            return null;
        }
    }

    //jwt에서 회원 구분 email 추출
    private String getUserEmail(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    public void checkRefreshToken(String userId, String refreshToken) {
        String redisRT = redisService.getValues(userId);
        if (!refreshToken.equals(redisRT)) {
            throw new RefreshTokenExpiredException();
        }
    }

    // access 토큰 만료시간을 체크 후,redis에 (blacklist) + accessToken, 계정, 만료기간을이 담긴
    // ValueOperation을 만들어 redis에 저장한다.
    // redis에서 유저 refreshtoken 값을 삭제한다
    public void logout(String userId, String accessToken) {
        Date expiration = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(accessToken).getBody().getExpiration();
        long expiredAccessTokenTime = expiration.getTime() - new Date().getTime();
        redisService.setValues(blackListATPrefix + accessToken, userId, Duration.ofMillis(expiredAccessTokenTime));
        redisService.deleteValues(userId);
    }
}
