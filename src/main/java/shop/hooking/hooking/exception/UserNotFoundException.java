package shop.hooking.hooking.exception;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import shop.hooking.hooking.global.constant.ResponseConstant;


public class UserNotFoundException extends ResourceNotFoundException {

	public UserNotFoundException() {
		super(ResponseConstant.NOTFOUND_USER);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}