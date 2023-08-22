package shop.hooking.hooking.exception;


import shop.hooking.hooking.global.constant.ResponseConstant;

public class CertificationCodeMismatchException extends IllegalArgumentException{
	public CertificationCodeMismatchException() {
	super(ResponseConstant.DUPLICATE_EMAIL);
}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
