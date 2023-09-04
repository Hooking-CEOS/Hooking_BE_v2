package shop.hooking.hooking.exception;

import shop.hooking.hooking.global.constant.ResponseConstant;

public class ScrapNotFoundException extends IllegalArgumentException {
    public ScrapNotFoundException() {
        super(ResponseConstant.SCRAP_NOT_FOUND);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
