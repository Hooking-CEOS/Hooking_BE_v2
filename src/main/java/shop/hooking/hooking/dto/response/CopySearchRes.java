package shop.hooking.hooking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CopySearchRes {
    private int totalNum;
    private String keyword;
    private String type;
    private List<CopyRes> data;


    @Builder
    public CopySearchRes(int totalNum, String keyword, String type, List<CopyRes> data) {
        this.totalNum = totalNum;
        this.keyword = keyword;
        this.type = type;
        this.data = data;
    }

}