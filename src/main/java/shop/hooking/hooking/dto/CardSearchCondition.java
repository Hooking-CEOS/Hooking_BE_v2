package shop.hooking.hooking.dto;

import lombok.Data;

@Data
public class CardSearchCondition {
    // 분위기, 나이대, 가격대, 제품군
    // 화면에서 이러한 조건들이 넘어옴

    private String mood;

    private String age;

    private String price;

    private String product;
}
