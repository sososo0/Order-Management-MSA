package com.sparta.msa_exam.order.framework.web.dto;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderProduct;

public record OrderUpdateOutputDTO(
    Long orderId,
    Long productId,
    int quantity
) {

    public static OrderUpdateOutputDTO toDTO(
        Order order,
        Long productId
    ) {

        OrderProduct orderProduct = order.getProducts().stream()
            .filter(product -> product.getProductId().equals(productId))
            .findFirst()
            .get();

        return new OrderUpdateOutputDTO(
            order.getId(),
            orderProduct.getProductId(),
            orderProduct.getQuantity()
        );
    }
}
