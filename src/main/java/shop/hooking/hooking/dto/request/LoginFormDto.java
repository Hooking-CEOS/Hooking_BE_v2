package shop.hooking.hooking.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LoginFormDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Builder
    public LoginFormDto(String email, String password) {
        this.email = email;
        this.password = password;
    }


}
