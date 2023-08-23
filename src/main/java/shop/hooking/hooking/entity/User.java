package shop.hooking.hooking.entity;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import shop.hooking.hooking.config.enumtype.Role;


import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Where(clause = "delete_flag=0")
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="kakao_id")
    @NotNull
    private Long kakaoId;

    @Column
    @NotNull
    private String nickname;

    @Column
    private String email;

    @Column
    private String image;

    @Column
    private String gender;

    @Column(name = "age_range")
    private String ageRange;

    @Column(name = "role")
    @NotNull
    private String role;

    @Column(name="delete_flag")
    @NotNull
    private Boolean deleteFlag;

    @Builder
    public User(Long kakaoId, String nickname, String email, String image, String gender, String ageRange, Role role) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        if(email!=null) this.email = email;
        this.image = image;
        if(gender!=null) this.gender = gender;
        if(ageRange!=null) this.ageRange = ageRange;
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
