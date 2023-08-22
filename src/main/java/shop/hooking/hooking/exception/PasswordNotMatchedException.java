package shop.hooking.hooking.exception;


import shop.hooking.hooking.global.constant.ResponseConstant;

public class PasswordNotMatchedException extends IllegalArgumentException {

	public PasswordNotMatchedException() {
		super(ResponseConstant.PASSWORD_NOT_MATCH);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}