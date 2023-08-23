package shop.hooking.hooking.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CopySearchRes {
    private int code;
    private String message;
    private List<CopySearchResult> data;
}