package shop.hooking.hooking.service;

//사용자 정보를 요청할 수 있는 access token 을 얻고나서 실행
//사용자의 정보들을 기반으로 가입 및 정보수정, 세션 저장등의 기능을 지원

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.response.OAuthAttributesRes;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.CustomException;
import shop.hooking.hooking.exception.ErrorCode;
import shop.hooking.hooking.repository.UserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    //회원가입 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        try{
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService(); //객체생성
            OAuth2User oAuth2User = delegate.loadUser(userRequest);// Oath2 정보를 가져옴

            OAuthAttributesRes attributes = OAuthAttributesRes.ofKakao(oAuth2User.getAttributes()); //회원정보 JSON 정제해서 반환

            Map<String, Object> newAttribute = updateAttributes(attributes);
            User user = saveOrUpdate(attributes); //정제된 회원정보 삽입
            String key = user.getRole();


            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(key)),
                    newAttribute, "id");

        }catch(OAuth2AuthenticationException e) {
            throw new CustomException(ErrorCode.EXCEPTION, e.getStackTrace().toString());
        }
    }

    //첫번째 로그인인지 확인
    private Map<String, Object> updateAttributes(OAuthAttributesRes attributes) {
        User user = userRepository.findMemberByKakaoId(attributes.getKakaoId());
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
    private User saveOrUpdate(OAuthAttributesRes attributes){
        User user = userRepository.findMemberByKakaoId(attributes.getKakaoId()); //db에 있는 유저인지 확인
        if(user!=null){ //있으면
            return user; //유저반환
        } else{
            userRepository.save(attributes.toEntity()); //없으면
            User newUser = userRepository.findMemberByKakaoId(attributes.getKakaoId()); //회원가입
            return newUser;
        }
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