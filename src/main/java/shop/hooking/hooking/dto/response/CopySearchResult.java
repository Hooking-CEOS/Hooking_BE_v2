package shop.hooking.hooking.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CopySearchResult {
    private String type;
    private List<CopyRes> data;

}