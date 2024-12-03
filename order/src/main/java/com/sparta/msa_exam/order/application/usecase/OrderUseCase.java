package com.sparta.msa_exam.order.application.usecase;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderForRead;
import com.sparta.msa_exam.order.application.domain.OrderForUpdate;

public interface OrderUseCase {
    Order createOrder(OrderForCreate orderForCreate);
    Order updateOrder(OrderForUpdate orderForUpdate);
    void failOrder(Long productId);
    Order getOrder(OrderForRead orderForRead);
}
