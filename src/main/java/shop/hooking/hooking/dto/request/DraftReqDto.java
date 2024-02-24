package shop.hooking.hooking.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.entity.Card;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DraftReqDto {

    private LocalDateTime createdAt;
    private String text;
    private String categoryName;

    @Builder
    public DraftReqDto(LocalDateTime createdAt, String text, String categoryName){
        this.createdAt=createdAt;
        this.text = text;
        this.categoryName=categoryName;
    }
}

