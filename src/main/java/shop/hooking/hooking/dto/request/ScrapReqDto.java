package shop.hooking.hooking.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.Folder;


@Getter
@NoArgsConstructor
public class ScrapReqDto {
    private Long cardId;
    private Long folderId;
    private String folderName;

    @Builder
    public ScrapReqDto(Card card, Folder folder){
        this.cardId=card.getId();
        this.folderId = folder.getId();
        this.folderName = folder.getName();
    }
}
