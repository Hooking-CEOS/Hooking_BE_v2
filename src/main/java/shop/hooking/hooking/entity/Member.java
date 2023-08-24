//package shop.hooking.hooking.entity;
//
//
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.GenericGenerator;
//import org.hibernate.validator.constraints.UniqueElements;
//import shop.hooking.hooking.global.entity.BaseTimeEntity;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//import java.util.UUID;
//
//
//
//@Getter
//@NoArgsConstructor
//@Entity
//public class Member extends BaseTimeEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @NotNull
//    @Column(unique = true)
//    private String email;
//
//    @JsonIgnore
//    @NotNull
//    private String password;
//
//    @NotNull
//    private String nickname;
//
//    private String role;
//
//    @Builder
//    public Member(String email, String password, String nickname){
//        this.email = email;
//        this.password = password;
//        this.nickname = nickname;
//        role = "USER";
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    //TODO : profile 연관관계 편의 메소드
//
//}
//
