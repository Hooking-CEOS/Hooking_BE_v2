package shop.hooking.hooking.exception;

import shop.hooking.hooking.global.constant.ResponseConstant;

public class CardNotFoundException extends IllegalArgumentException {
    public CardNotFoundException() {
        super(ResponseConstant.CARD_NOT_FOUND);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

