package com.sparta.msa_exam.order.application.usecase;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;

public interface OrderUseCase {
    Order createOrder(OrderForCreate orderForCreate);
}
