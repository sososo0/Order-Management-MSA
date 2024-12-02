package com.sparta.msa_exam.order.application.inputport;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderForUpdate;
import com.sparta.msa_exam.order.application.outputport.OrderOutputPort;
import com.sparta.msa_exam.order.application.usecase.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderInputPort implements OrderUseCase {

    private final OrderOutputPort orderOutputPort;

    @Override
    public Order createOrder(OrderForCreate orderForCreate) {
        return orderOutputPort.saveOrder(orderForCreate);
    }

    @Override
    public Order updateOrder(OrderForUpdate orderForUpdate) {
        return orderOutputPort.updateOrder(orderForUpdate);
    }
}
