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


    NULL_INPUT_VALUE(HttpStatus.BAD_REQUEST, 4000, "input 값이 비어있습니다."), // Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 4001, "err.invalid.input.value"), // Bad Request
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 4002, "타당하지 않은 타입입니다."), // Bad Request
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, 4004, "err.entity.not.found"), // Bad Request
    DUPLICATE_DATA(HttpStatus.CONFLICT, 4005, "err.duplicate.data"),

    NULL_TYPE_VALUE(HttpStatus.BAD_REQUEST, 4100, "타입이 null 값입니다."), // Bad Request
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, 4101, "이미 팔로우 중인 상태입니다.")

    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
