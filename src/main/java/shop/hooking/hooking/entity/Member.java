package shop.hooking.hooking.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import shop.hooking.hooking.global.entity.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;



@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 16)
    private UUID id;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

/*
    @Column(unique = true, length = 12)
    private Long uid;*/

    @NotNull
    private String email;

    @JsonIgnore
    @NotNull
    private String password;

    @NotNull
    private String nickname;



    private String role;

    @Builder
    public Member(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        role = "USER";
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setId(UUID id){
        this.id = id;
    }

    //TODO : profile 연관관계 편의 메소드

}

