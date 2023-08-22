package shop.hooking.hooking.exception;


import shop.hooking.hooking.global.constant.ResponseConstant;

public class DuplicateEmailException extends IllegalArgumentException {
	public DuplicateEmailException() {
		super(ResponseConstant.DUPLICATE_EMAIL);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
