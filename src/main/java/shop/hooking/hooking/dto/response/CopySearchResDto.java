package shop.hooking.hooking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CopySearchResDto {
    private int totalNum;
    private String keyword;
    private String type;
    private List<CopyResDto> data;
    private Long randomSeed;


    @Builder
    public CopySearchResDto(int totalNum, String keyword, String type, List<CopyResDto> data, Long randomSeed) {
        this.totalNum = totalNum;
        this.keyword = keyword;
        this.type = type;
        this.data = data;
        this.randomSeed=randomSeed;
    }

}