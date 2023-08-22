package shop.hooking.hooking.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class CustomException extends ResponseStatusException {
    public CustomException(ErrorCode errorCode, String reason) {
        super(errorCode.getHttpStatus(), reason);
    }
}