package shop.hooking.hooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BrandResDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandDto {
        Long brandId;
        String brandName;
        String brandLink;
        String randomCard;
        List<String> mood;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandDetailDto {
        Long brandId;
        String brandName;
        String brandLink;
        String brandIntro;
        List<String> randomCard;
        List<String> mood;
        List<cardDto> card;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class cardDto{
        private Long id;
        private String brandName;
        private String text;
        private Integer scrapCnt;
        private Integer isScrap;
        private LocalDateTime createdAt;

        private String cardLink;
    }

}
