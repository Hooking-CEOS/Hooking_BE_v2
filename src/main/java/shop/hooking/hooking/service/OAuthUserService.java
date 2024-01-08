package shop.hooking.hooking.service;

//사용자 정보를 요청할 수 있는 access token 을 얻고나서 실행
//사용자의 정보들을 기반으로 가입 및 정보수정, 세션 저장등의 기능을 지원

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.SessionUser;
import shop.hooking.hooking.dto.response.OAuthAttributesResDto;
import shop.hooking.hooking.dto.response.OAuthUserResDto;
import shop.hooking.hooking.entity.Role;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;
import shop.hooking.hooking.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        log.info("loadUser method !");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService(); //객체생성
        OAuth2User oAuth2User = delegate.loadUser(userRequest);// Oath2 정보를 가져옴
        OAuthAttributesResDto attributes = OAuthAttributesResDto.ofKakao(oAuth2User.getAttributes()); //회원정보 JSON 정제해서 반환

        log.info("attributes: " + attributes);
        log.info("attributes.getKakaoId: " + attributes.getKakaoId());
        User user = saveOrUpdate(attributes);
        user.setRole(String.valueOf(Role.USER));
        httpSession.setAttribute("user", new SessionUser(user));


        Collection<? extends GrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey()));

        return new DefaultOAuth2User(
                authorities,
                attributes.getAttributes(),
                "id"
        );
    }

    //첫번째 로그인인지 확인
    private Map<String, Object> updateAttributes(OAuthAttributesResDto attributes) {
        User user = userRepository.findUserByKakaoId(attributes.getKakaoId())
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
        Map<String, Object> newAttribute = new HashMap<String, Object>();
        newAttribute.putAll(attributes.getAttributes());

        if(user==null) {
            newAttribute.put("firstLogin", true);
        }
        else {
            newAttribute.put("firstLogin", false);
        }
        return newAttribute;
    }

    //인증된 유저 DTO 반환
    private User saveOrUpdate(OAuthAttributesResDto attributes) {
        log.info("saveOrUpdate method !");
        Optional<User> User = userRepository.findUserByKakaoId(attributes.getKakaoId());
        if (User.isPresent()) {
            User user = User.get();
            log.info("user: " + user);
            log.info("user is");
            return user;
        } else {
            log.info("user isn't");
            User newUser = userRepository.save(attributes.toEntity());
            return newUser;
        }
    }

    public void selectRole(HttpServletRequest httpRequest, String role) {
        try {
            // 여기에 사용자의 역할을 업데이트하는 로직을 추가
            User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
            System.out.println("Picked User: " + user);
            user.setRole(role);
            userRepository.save(user);

            Role updatedRole = user.getRole();
            System.out.println("Updated User: " + user);
            System.out.println("Updated User Role: " + updatedRole);

        } catch (Exception e) {
            e.printStackTrace();
            // 업데이트 실패 시 예외를 다시 던짐
            throw new RuntimeException("Failed to update role: " + e.getMessage());
        }
    }

    public OAuthUserResDto getUser(HttpServletRequest httpRequest) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        return user != null ? OAuthUserResDto.builder().user(user).build() : null;
    }


//    @Transactional
//    public String deleteSessionMember(Long memberId) {
//        User user = findMemberEntity(memberId);
//        user.updateDeleteFlag();
//        return "삭제 완료";
//    }
//
//    public User findMemberEntity(Long memberId) {
//        return userRepository.findMemberByMemberIdAndDeleteFlagIsFalse(memberId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, null));
//    }


}