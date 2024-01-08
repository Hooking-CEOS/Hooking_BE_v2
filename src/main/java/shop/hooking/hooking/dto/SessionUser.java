package shop.hooking.hooking.dto;

import lombok.Getter;
import shop.hooking.hooking.entity.User;
import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String nickname;
    private String image;

    public SessionUser(User user) {
        this.nickname = user.getNickname();
        this.image = user.getImage();
    }
}
