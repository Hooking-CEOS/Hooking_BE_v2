package shop.hooking.hooking.exception;


import shop.hooking.hooking.global.constant.ResponseConstant;

public class BeforePasswordNotMatchException extends IllegalArgumentException {
	public BeforePasswordNotMatchException() {
		super(ResponseConstant.BEFORE_PASSWORD_NOT_MATCH);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
