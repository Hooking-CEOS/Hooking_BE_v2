package shop.hooking.hooking.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER", "유저"),
    GUEST("ROLE_GUEST", "손님"),
    BRAND("ROLE_BRAND", "브랜드");

    private final String key;
    private final String title;
}
