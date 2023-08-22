package shop.hooking.hooking.exception;


import shop.hooking.hooking.global.constant.ResponseConstant;

public class RefreshTokenExpiredException extends RuntimeException {
	public RefreshTokenExpiredException() {
		super(ResponseConstant.EXPIRED_REFRESHTOKEN);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
