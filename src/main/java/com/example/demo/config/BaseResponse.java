package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.example.demo.config.BaseResponseStatus.*;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String message;
    private final int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;


    // 요청에 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS_WITH_DATA.isSuccess();
        this.code = SUCCESS_WITH_DATA.getCode();
        this.message = SUCCESS_WITH_DATA.getMessage();
        this.result = result;
    }

    // 등록/수정에 성공한 경우
    public BaseResponse(T result ,BaseResponseStatus baseResponseStatus) {
        this.isSuccess = baseResponseStatus.isSuccess();
        this.code = baseResponseStatus.getCode();
        this.message = baseResponseStatus.getMessage();
        this.result = result;
    }



    // 요청에 실패한 경우
    // 성공했지만 추가적인 result가 없는 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.code = status.getCode();
        this.message = status.getMessage();
    }


    // Validation 실패한 경우
    public BaseResponse(BaseResponseStatus status, String message){
        this.isSuccess = status.isSuccess();
        this.message = message;
        this.code = status.getCode();
    }

}

