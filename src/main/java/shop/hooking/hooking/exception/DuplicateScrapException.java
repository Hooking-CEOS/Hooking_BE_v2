package shop.hooking.hooking.exception;

import shop.hooking.hooking.global.constant.ResponseConstant;

public class DuplicateScrapException extends IllegalArgumentException {

    public  DuplicateScrapException() {
        super(ResponseConstant.DUPLICATE_SCRAP);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
