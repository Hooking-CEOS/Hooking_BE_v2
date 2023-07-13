package shop.hooking.hooking.dto.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.hooking.hooking.entity.Brand;


import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class CopyRes {
    private Long id;
    private String brandName;
    private String text;
    private Integer scrapCnt;
    private LocalDateTime createdAt;

    private String type;

    private List<Integer> index;


    @Builder
    @QueryProjection
    public CopyRes(Long id, Brand brand, String text, Integer scrapCnt,LocalDateTime createdAt) {
        this.id = id;
        this.brandName = brand.getBrandName();
        this.text = text;
        this.scrapCnt = scrapCnt;
        this.createdAt = createdAt;
    }


}

