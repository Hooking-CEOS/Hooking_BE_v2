package shop.hooking.hooking.dto.response;

        import lombok.Builder;
        import lombok.Getter;
        import lombok.NoArgsConstructor;

        import java.util.List;

@Getter
@NoArgsConstructor
public class CategoryDraftResDto {
    private List<DraftResDto> drafts;
    private int totalNum;

    private String categoryName;

    @Builder
    public CategoryDraftResDto(List<DraftResDto> drafts, int totalNum, String categoryName) {
        this.drafts=drafts;
        this.totalNum=totalNum;
        this.categoryName=categoryName;
    }
}
