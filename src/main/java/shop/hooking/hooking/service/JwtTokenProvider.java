package shop.hooking.hooking.service;



import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Component;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.UserRepository;
import shop.hooking.hooking.dto.response.OAuthUserResponseDTO;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;


//토큰 생성

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private final UserRepository userRepository;

    @Value("yexxi2118#")
    private String SECRET_KEY;

    private final long ACCESS_TOKEN_VALID_TIME = 24 * 60 * 60 * 1000L; //5시간

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


    public Authentication getAuthentication(String token) {
        User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)));
        OAuthUserResponseDTO resDTO = OAuthUserResponseDTO.builder().user(user).build();
        return new UsernamePasswordAuthenticationToken(resDTO, "", resDTO.getAuthorities());
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



    public OAuthUserResponseDTO getKakaoInfo(HttpServletRequest request) {
        String token = resolveToken(request);
        if(validateToken(token,request)) {
            User user = userRepository.findMemberByKakaoId(Long.parseLong(getUserPk(token)));
            return new OAuthUserResponseDTO(user);
        }
        else {
            return null;
        }
    }
}

