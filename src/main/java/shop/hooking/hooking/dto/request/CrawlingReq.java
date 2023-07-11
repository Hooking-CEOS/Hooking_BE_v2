package shop.hooking.hooking.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.entity.Card;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CrawlingReq {
    private String text;
    private LocalDateTime createdAt;


}
