package shop.hooking.hooking.service;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import shop.hooking.hooking.dto.SessionUserDTO;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.UserRepository;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;


//토큰 생성

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private final UserRepository userRepository;

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    private final long ACCESS_TOKEN_VALID_TIME = 5 * 60 * 60 * 1000L; //5시간

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


    // token 가공해서 정보 추출
    public Authentication getAuthentication(String token) {
        User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)));
        UserDetails sessionUserDTO = SessionUserDTO.builder().user(user).build();
        return new UsernamePasswordAuthenticationToken(sessionUserDTO, "", sessionUserDTO.getAuthorities());
    }


    public String getUserPk(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    // HTTP 요청 안에서 헤더 찾아서 토큰 가져옴
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date()); // 유효하면 return
        } catch (Exception e){
            return false; //유효하지 않은 경우
        }
    }

    public SessionUserDTO getUserInfoByToken(HttpServletRequest request) {
        String token = resolveToken(request);
        if(validateToken(token)) {
            User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)));
            return new SessionUserDTO(user);
        }
        else {
            return null;
        }
    }
}

