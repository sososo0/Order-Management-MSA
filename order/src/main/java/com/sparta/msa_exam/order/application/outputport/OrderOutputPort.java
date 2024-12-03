package com.sparta.msa_exam.order.application.outputport;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderForUpdate;
import java.util.Optional;

public interface OrderOutputPort {
    Order saveOrder(OrderForCreate orderForCreate, int totalCost);
    Order updateOrder(OrderForUpdate orderForUpdate);
    Optional<Order> findByOrderId(Long orderId);
}
