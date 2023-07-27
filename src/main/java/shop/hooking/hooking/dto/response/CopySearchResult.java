package shop.hooking.hooking.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CopySearchResult {
    private int totalNum;
    private String keyword;
    private String type;
    private List<CopyRes> data;

}