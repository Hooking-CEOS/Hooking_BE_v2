package shop.hooking.hooking.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class CategoryReqDto {
    private LocalDateTime createdAt;
    private String categoryName;

    @Builder
    public CategoryReqDto(LocalDateTime createdAt, String categoryName){
        this.createdAt=createdAt;
        this.categoryName=categoryName;
    }
}
