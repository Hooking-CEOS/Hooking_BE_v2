package shop.hooking.hooking.exception;


import shop.hooking.hooking.global.constant.ResponseConstant;

public class DuplicateNicknameException extends IllegalArgumentException {
	public DuplicateNicknameException() {
		super(ResponseConstant.DUPLICATE_NICKNAME);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
