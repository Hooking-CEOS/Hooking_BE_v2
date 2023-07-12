package shop.hooking.hooking.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CrawlingData {
    private String text;
    private LocalDateTime createdAt;
    private Long brandId;

}
