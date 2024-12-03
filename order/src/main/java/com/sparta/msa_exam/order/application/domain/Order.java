package com.sparta.msa_exam.order.application.domain;

import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class Order {
    Long id;
    Long userId;
    OrderStatus orderStatus;
    List<OrderProduct> products;
    int orderTotalPrice;
    LocalDateTime orderTime;

    public Order(
        Long id,
        Long userId,
        OrderStatus orderStatus,
        List<OrderProduct> products,
        int orderTotalPrice,
        LocalDateTime orderTime
    ) {
        this.id = id;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.products = products;
        this.orderTotalPrice = orderTotalPrice;
        this.orderTime = orderTime;
    }
}
