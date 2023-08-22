package shop.hooking.hooking.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResDto {
    private String nickname;

    private String accessToken;

    @Builder
    public LoginResDto(String nickname, String accessToken)
    {
        this.nickname = nickname;
        this.accessToken = accessToken;
    }
}
