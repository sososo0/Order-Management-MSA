package com.sparta.msa_exam.order.domain.model.vo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderStatus {

    ORDER_CREATED("주문 생성"),
    ORDER_SUCCESS("주문 성공"),
    ORDER_FAIL("주문 실패");

    private final String message;
}
