package shop.hooking.hooking.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.hooking.hooking.entity.User;

@Getter
public class OAuthUserResponseDTO  {
    private String nickname;
    private String email;
    private String picture;

    @Builder
    public OAuthUserResponseDTO(User user){
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.picture = user.getImage();
    }
}