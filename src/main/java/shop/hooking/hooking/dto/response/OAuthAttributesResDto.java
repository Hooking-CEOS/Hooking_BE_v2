package shop.hooking.hooking.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.hooking.hooking.entity.User;
import java.util.Map;



@Getter
public class OAuthAttributesResDto {
    private Map<String, Object> attributes;
    private Long kakaoId;
    private String nickname;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributesResDto(Map<String, Object> attributes, Long kakaoId, String nickname,
                                 String email, String picture) {
        this.kakaoId = kakaoId;
        this.attributes = attributes;
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributesResDto ofKakao(Map<String, Object> attributes){
        Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) response.get("profile");

        return OAuthAttributesResDto.builder()
                .kakaoId((Long)attributes.get("id"))
                .nickname((String)profile.get("nickname"))
                .email((String)response.get("email"))
                .picture((String)profile.get("profile_image_url"))
                .attributes(attributes)
                .build();
    }

    public User toEntity() {
        User user = User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .image(picture)
                .email(email)
                .build();

        return user;
    }
}
