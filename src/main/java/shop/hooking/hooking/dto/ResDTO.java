package shop.hooking.hooking.dto;

import lombok.Builder;
import lombok.Getter;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import shop.hooking.hooking.entity.User;

@Getter
public class ResDTO  {
    private String nickname;

    private String email;

    private String picture;

    @Builder
    public ResDTO(User user){
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.picture = user.getImage();

    }
}
