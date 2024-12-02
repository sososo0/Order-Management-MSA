package com.sparta.msa_exam.order.framework.web.dto;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrderReadOutputDTO(
    Long orderId,
    OrderStatus orderStatus,
    List<ProductDetail> productDetails,
    int orderTotalPrice,
    LocalDateTime orderTime
) {

    public record ProductDetail(
        Long productId,
        int quantity
    ) {

    }

    public static OrderReadOutputDTO toDTO(
        Order order
    ) {
        List<ProductDetail> productDetails = order.getProducts().stream()
            .map(product -> new ProductDetail(
                product.getProductId(),
                product.getQuantity()
            )).collect(Collectors.toList());

        return new OrderReadOutputDTO(
            order.getId(),
            order.getOrderStatus(),
            productDetails,
            order.getOrderTotalPrice(),
            order.getOrderTime()
        );
    }
}
