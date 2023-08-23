package shop.hooking.hooking.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class EmailGetDto {
    @NotBlank
    private String email;
}

