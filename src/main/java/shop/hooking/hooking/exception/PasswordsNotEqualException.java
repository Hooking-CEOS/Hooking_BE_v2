package shop.hooking.hooking.exception;


import shop.hooking.hooking.global.constant.ResponseConstant;

public class PasswordsNotEqualException extends IllegalArgumentException {

	public PasswordsNotEqualException() {
		super(ResponseConstant.PASSWORDS_NOT_EQUAL);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
