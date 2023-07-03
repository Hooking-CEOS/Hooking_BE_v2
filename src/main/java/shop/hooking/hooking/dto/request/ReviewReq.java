package shop.hooking.hooking.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ReviewReq {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WriteReviewDto {
        private String title; // 건의사항 제목
        private String content; // 건의사항 내용


    }
}
