package shop.hooking.hooking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CategoryResDto {
    private List<CategoryDraftResDto> data;


    @Builder
    public CategoryResDto(List<CategoryDraftResDto> data) {
        this.data=data;

    }
}

