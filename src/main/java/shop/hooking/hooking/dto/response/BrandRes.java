package shop.hooking.hooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.entity.Card;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BrandRes {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandDto {
        Long brandId; // 브랜드 아이디
        String brandName; // 브랜드 이름
        String brandLink; // 브랜드 인스타 링크
        String randomCard; // 랜덤 카피라이팅 카드
        List<String> mood; // 브랜드 무드 3개
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandDetailDto {
        Long brandId; // 브랜드 아이디
        String brandName; // 브랜드 이름
        String brandLink; // 브랜드 인스타 링크
        String brandIntro; // 브랜드 한줄소개
        List<String> randomCard; // 랜덤 카피라이팅 카드에서 본문만 3개
        List<String> mood; // 브랜드 무드 3개
        List<cardDto> card; // 브랜드 전체 카피라이팅 카드
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
        private LocalDateTime createdAt;
    }

}
