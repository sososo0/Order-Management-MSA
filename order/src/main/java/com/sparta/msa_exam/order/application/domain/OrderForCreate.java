package com.sparta.msa_exam.order.application.domain;

import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import java.util.List;

public record OrderForCreate(
    List<OrderProductForCreate> products,
    OrderStatus status,
    Long userId,
    String role
) {

}
