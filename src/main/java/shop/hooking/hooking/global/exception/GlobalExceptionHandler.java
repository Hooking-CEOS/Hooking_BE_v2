package shop.hooking.hooking.global.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.hooking.hooking.exception.BadTokenRequestException;
import shop.hooking.hooking.exception.UserNotFoundException;


import java.time.DateTimeException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*================== Basic Exception ==================*/
    @ExceptionHandler(RuntimeException.class)
    protected final ResponseEntity<ErrorResponse> handleRunTimeException(RuntimeException e) {
        final ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(ErrorCode.RUNTIME_EXCEPTION)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(DateTimeException.class)
    protected final ResponseEntity<ErrorResponse> handleDateTimeException(DateTimeException e) {
        final ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(ErrorCode.BAD_DATE_REQUEST)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // vaild 오류
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected final ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return ErrorResponse.toErrorResponseEntity(ErrorCode.USER_NOT_FOUND, e.getMessage());
    }



    /*================== Token Exception ==================*/
    @ExceptionHandler(BadTokenRequestException.class)
    protected final ResponseEntity<ErrorResponse> handleBadTokenRequestException(BadTokenRequestException e) {
        return ErrorResponse.toErrorResponseEntity(ErrorCode.TOKEN_VALIDATE_FAILURE, e.getMessage());
    }



}
