package com.sparta.msa_exam.order.application.domain;

import lombok.Getter;

@Getter
public class OrderProduct {
    private Long id;
    private Long productId;
    private int quantity;

    public OrderProduct(
        Long id,
        Long productId,
        int quantity
    ) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }
}
