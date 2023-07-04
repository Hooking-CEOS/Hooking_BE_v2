package shop.hooking.hooking.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.entity.Brand;
import shop.hooking.hooking.entity.Card;

@Getter
@NoArgsConstructor
public class CopyReq {
    private Long cardId;

    @Builder
    public CopyReq(Card card){
        this.cardId=card.getId();
    }
}
