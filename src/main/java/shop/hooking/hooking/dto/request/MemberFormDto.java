package shop.hooking.hooking.dto.request;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import shop.hooking.hooking.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFormDto {

    @NotBlank(message="이름은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank
    @Length(min = 2, max = 16)
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!.?,])[A-Za-z\\d!.?,]{2,16}$",
//            message = "16자 이내의 영문자 및 숫자와 ?,!,., , 특수문자로 입력해주세요.")
    private String password;


    @Builder
    public MemberFormDto(String email, String password,String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();
    }

    public void setPassword(String password){
        this.password = password;
    }

}
