package com.ssafy.clonenova.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /*
    TODO : 프론트 측과 ErrorCode 맞춰야함
    - 아래 코드는 예시로 만들어 둔 거여서 수정 필요!
    */


    NULL_INPUT_VALUE(HttpStatus.BAD_REQUEST, 4000, "err.id or password.essential.Input."), // Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 4001, "err.invalid.input.value"), // Bad Request
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 4002, "err.invalid.type.value"), // Bad Request
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, 4003, "err.entity.not.found"), // Bad Request
    DUPLICATE_DATA(HttpStatus.CONFLICT, 4004, "err.duplicate.data")

    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
