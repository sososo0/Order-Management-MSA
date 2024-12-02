package com.sparta.msa_exam.order.framework.web.dto;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrderCreateOutputDTO(
    Long orderId,
    OrderStatus status,
    OrderDetails orderDetails,
    int orderTotalPrice,
    LocalDateTime orderTime
) {

    public record OrderDetails(
        List<ProductDetail> products
    ) {
        public record ProductDetail(
            Long productId,
            int quantity
        ) {

        }
    }

    public static OrderCreateOutputDTO toDTO(
        Order order
    ) {
        List<OrderDetails.ProductDetail> productDetails = order.getProducts().stream()
            .map(product -> new OrderDetails.ProductDetail(
                product.getProductId(),
                product.getQuantity()
            )).collect(Collectors.toList());

        OrderDetails orderDetails = new OrderDetails(
            productDetails
        );

        return new OrderCreateOutputDTO(
            order.getId(),
            order.getOrderStatus(),
            orderDetails,
            order.getOrderTotalPrice(),
            order.getOrderTime()
        );
    }

}
