package shop.hooking.hooking.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import shop.hooking.hooking.entity.User;

import java.util.Collection;
import java.util.Collections;

@Getter
public class OAuthUserRes {
    private String nickname;

    private String email;

    private String picture;

    private String gender;

    private String ageRange;

    private String role;

    @Builder
    public OAuthUserRes(User user){
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.picture = user.getImage();
        this.gender = user.getGender();
        this.ageRange = user.getAgeRange();
        this.role = user.getRole();
    }


//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
//    }
}
