package shop.hooking.hooking.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class DraftResDto {

    private LocalDateTime createdAt;
    private String text;

    private String categoryName;

    @Builder
    @QueryProjection
    public DraftResDto(LocalDateTime createdAt, String text, String categoryName){
        this.createdAt=createdAt;
        this.text = text;
        this.categoryName=categoryName;
    }
}