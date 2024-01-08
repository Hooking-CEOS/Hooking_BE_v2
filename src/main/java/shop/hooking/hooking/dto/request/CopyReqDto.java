package shop.hooking.hooking.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.entity.Card;

@Getter
@NoArgsConstructor
public class CopyReqDto {
    private Long cardId;

    @Builder
    public CopyReqDto(Card card){
        this.cardId=card.getId();
    }
}
