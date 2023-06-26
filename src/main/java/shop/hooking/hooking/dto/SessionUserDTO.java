package shop.hooking.hooking.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shop.hooking.hooking.entity.User;
import java.util.Collection;
import java.util.Collections;


//인증된 사용자 유
@Getter
public class SessionUserDTO implements UserDetails {
    private Long Id;
    private Long kakaoId;
    private String nickname;
    private String role;

    @Builder
    public SessionUserDTO(User user) {
        this.Id = user.getId();
        this.kakaoId = user.getKakaoId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
