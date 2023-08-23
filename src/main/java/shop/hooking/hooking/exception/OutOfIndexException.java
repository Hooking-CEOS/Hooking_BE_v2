package shop.hooking.hooking.exception;

import shop.hooking.hooking.global.constant.ResponseConstant;

public class OutOfIndexException extends IllegalArgumentException {

    public OutOfIndexException() {
        super(ResponseConstant.OUT_OF_INDEX);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
