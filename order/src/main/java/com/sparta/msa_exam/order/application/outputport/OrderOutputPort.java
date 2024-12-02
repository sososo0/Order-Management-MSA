package com.sparta.msa_exam.order.application.outputport;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderForUpdate;

public interface OrderOutputPort {
    Order saveOrder(OrderForCreate orderForCreate);
    Order updateOrder(OrderForUpdate orderForUpdate);
}
