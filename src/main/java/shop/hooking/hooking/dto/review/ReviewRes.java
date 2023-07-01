package shop.hooking.hooking.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewRes {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewDto {
        String content; // 건의사항 내용
        LocalDateTime writeTime; // 작성시간

    }
}
