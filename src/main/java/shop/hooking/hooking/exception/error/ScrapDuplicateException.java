package shop.hooking.hooking.exception.error;

import shop.hooking.hooking.exception.BaseErrorException;
import shop.hooking.hooking.exception.ErrorCode;

public class ScrapDuplicateException extends BaseErrorException {

    public static final ScrapDuplicateException EXCEPTION = new ScrapDuplicateException();

    public ScrapDuplicateException() {
        super(ErrorCode.CANNOT_DUPLICATE_SCRAP);
    }
}