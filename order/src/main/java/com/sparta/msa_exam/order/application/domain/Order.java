package com.sparta.msa_exam.order.application.domain;

import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class Order {
    Long id;
    OrderStatus orderStatus;
    List<OrderProduct> products;
    int orderTotalPrice;
    LocalDateTime orderTime;
    // TODO: USER 추가하기

    public Order(
        Long id,
        OrderStatus orderStatus,
        List<OrderProduct> products,
        int orderTotalPrice,
        LocalDateTime orderTime
    ) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.products = products;
        this.orderTotalPrice = orderTotalPrice;
        this.orderTime = orderTime;
    }
}
