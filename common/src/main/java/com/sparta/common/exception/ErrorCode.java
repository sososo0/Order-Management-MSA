package com.sparta.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // auth
    USERNAME_BAD_REQUEST(400, "USERNAME_BAD_REQUEST", "조건에 맞지 않는 사용자명입니다."),
    PASSWORD_BAD_REQUEST(400, "PASSWORD_BAD_REQUEST", "조건에 맞지 않는 비밀번호입니다."),
    USERNAME_ALREADY_EXIST(409, "USERNAME_ALREADY_EXIST", "이미 존재하는 사용자 명입니다."),
    WRONG_PASSWORD(400, "WRONG_PASSWORD", "잘못된 비밀번호입니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED", "로그인을 해주세요."),
    USER_NOT_EXIST(404, "USER_NOT_EXIST", "존재하지 않는 사용자입니다."),
    FORBIDDEN(403, "FORBIDDEN", "접근 권한이 없습니다."),

    // product
    QUANTITY_BAD_REQUEST(400, "QUANTITY_BAD_REQUEST", "잘못된 수량 요청입니다."),
    INVALID_PRODUCT(400, "INVALID_PRODUCT", "유효하지 않은 상품입니다."),
    PRODUCT_NOT_EXIST(404, "PRODUCT_NOT_EXIST", "존재하지 않는 상품입니다."),

    // order
    ORDER_ACCESS_DENIED(403, "ACCESS_DENIED", "접근이 불가한 주문입니다."),
    INVALID_ORDER(400, "INVALID_ORDER", "유효하지 않은 주문입니다."),
    ORDER_NOT_EXIST(404, "ORDER_NOT_EXIST", "존재하지 않는 주문입니다."),

    // example
    INVALID_PARAMETER(400, "INVALID_PARAMETER", "클라이언트에서 요청한 파라미터의 형식이나 내용에 오류가 있는 경우"),
    INVALID_RESOURCE(400, "INVALID_{RESOURCE}", "클라이언트에서 요청한 리소스의 내용에 오류가 있는 경우"),
    ACCESS_DENIED(403, "ACCESS_DENIED", "클라이언트가 요청하는 리소스에 대한 접근이 제한되는 경우"),
    RESOURCE_NOT_EXIST(404, "{RESOURCE}_NOT_EXIST", "클라이언트가 요청한 특정 리소스를 찾을 수 없는 경우"),
    RESOURCE_ALREADY_EXIST(409, "{RESOURCE}_ALREADY_EXIST", "클라이언트가 요청한 리소스가 이미 존재하는 경우"),

    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류");

    private final int code;
    private final String description;
    private final String detailMessage;
}

