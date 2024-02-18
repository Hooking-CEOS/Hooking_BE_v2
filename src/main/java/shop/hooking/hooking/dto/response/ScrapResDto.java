package shop.hooking.hooking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.Folder;


@Getter
@NoArgsConstructor
public class ScrapResDto {
    private Long cardId;
    private Long folderId;

    @Builder
    public ScrapResDto(Card card, Folder folder){
        this.cardId=card.getId();
        this.folderId = folder.getId();
    }
}