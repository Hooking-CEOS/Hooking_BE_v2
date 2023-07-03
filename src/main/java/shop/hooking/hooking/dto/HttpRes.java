package shop.hooking.hooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "message"})
public class HttpRes<T> {
    private int code;
    private T message;


    //오류일 경우
    public HttpRes(int code, T message) {
        this.code = code;
        this.message = message;
    }


    //성공일 경우
    public HttpRes(T message) {
        this.code = 200;
        this.message = message;
    }
}
