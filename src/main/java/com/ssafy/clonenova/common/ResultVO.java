package com.ssafy.clonenova.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ResultVO<T> implements Serializable {

    private final int code;
    private final String message;
    private final T data;

    @JsonCreator
    public ResultVO(int code, T data) {
        this(code, null, data);
    }

    public ResultVO(T data) {
        this(200, data);
    }

    public ResultVO(ErrorCode errorCode, T data) {
        this(errorCode.getCode(), errorCode.getMessage(), data);
    }

    public static ResultVO<Void> success() { return new ResultVO<>(200, "success", null); }

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200,"success", data);
    }
}
