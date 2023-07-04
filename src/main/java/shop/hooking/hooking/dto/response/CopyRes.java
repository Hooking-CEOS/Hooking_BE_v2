package shop.hooking.hooking.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.hooking.hooking.entity.Brand;


import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class CopyRes {
    private Long id;
    private String brandName;
    private String text;
    private Integer scrapCnt;
    private LocalDateTime createdAt;

    @Builder
    public CopyRes(Brand brand, String text, Integer scrapCnt, LocalDateTime createdAt) {
        this.brandName = brand.getBrandName();
        this.text = text;
        this.scrapCnt = scrapCnt;
        this.createdAt = createdAt;
    }


}

