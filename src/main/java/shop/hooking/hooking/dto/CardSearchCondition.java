package shop.hooking.hooking.dto;

import lombok.Data;

import java.util.List;

@Data
public class CardSearchCondition {
    // 분위기, 나이대, 가격대, 제품군
    // 화면에서 이러한 조건들이 넘어옴

    private List<String> moods;

    private List<String> ages;

    private List<String> prices;

    private List<String> products;
}
