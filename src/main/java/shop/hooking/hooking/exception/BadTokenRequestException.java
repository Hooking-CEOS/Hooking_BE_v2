package shop.hooking.hooking.exception;


import shop.hooking.hooking.global.constant.ResponseConstant;


public class BadTokenRequestException extends RuntimeException {
    public BadTokenRequestException() {
        super(ResponseConstant.BAD_TOKEN_REQUEST);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
