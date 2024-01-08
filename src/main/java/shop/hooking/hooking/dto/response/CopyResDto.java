package shop.hooking.hooking.dto.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.hooking.hooking.entity.Brand;
import java.util.*;


import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class CopyResDto implements Comparable<CopyResDto>{
    private Long id;
    private String brandName;
    private String text;
    private Integer scrapCnt;
    private LocalDateTime createdAt;
    private List<Integer> index;
    private String cardLink;
    private LocalDateTime scrapTime;
    private Integer isScrap;


    @Builder
    @QueryProjection
    public CopyResDto(Long id, Brand brand, String text, Integer scrapCnt, LocalDateTime createdAt, String cardLink) {
        this.id = id;
        this.brandName = brand.getBrandName();
        this.text = text;
        this.scrapCnt = scrapCnt;
        this.createdAt = createdAt;
        this.cardLink = cardLink;


    }

    public CopyResDto(Long id, Brand brand, String text, LocalDateTime createdAt, String cardLink) {
        this.id = id;
        this.brandName = brand.getBrandName();
        this.text = text;
        this.scrapCnt = 0;
        this.createdAt = createdAt;
        this.cardLink = cardLink;
    }

//        this.scrapTime = scrapTime;
//        this.isScrap = isScrap;




    @Override
    public int compareTo(CopyResDto other) {
        return other.scrapTime.compareTo(this.scrapTime); // 역순으로 정렬
    }


}