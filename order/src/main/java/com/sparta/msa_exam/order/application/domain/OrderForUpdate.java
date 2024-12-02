package com.sparta.msa_exam.order.application.domain;

public record OrderForUpdate(
    Long orderId,
    Long productId,
    int quantity
) {

}
