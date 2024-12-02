package com.sparta.msa_exam.order.application.domain;

public record OrderProductForCreate(
    Long productId,
    int quantity
) {

}
