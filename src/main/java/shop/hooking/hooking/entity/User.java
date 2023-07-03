package shop.hooking.hooking.entity;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.config.Role;
import shop.hooking.hooking.entity.Review;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    @NotNull
    private Long kakaoId;

    @Column
    @NotNull
    private String nickname;

    @Column
    private String email;

    @Column
    private String image;

    @Column(name = "role")
    @NotNull
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @Column
    @NotNull
    private Boolean deleteFlag;

    @Builder
    public User(Long kakaoId, String nickname, String email, String image, Role role) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        if(email!=null) this.email = email;
        this.image = image;
        this.role = role.getValue();
        this.deleteFlag = false;
    }


    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updateImage(String fileurl) {
        this.image = fileurl;
    }

    public void updateRole(Role role) {
        this.role = role.getValue();
    }

    public void updateDeleteFlag() {
        this.deleteFlag = true;
    }
}
