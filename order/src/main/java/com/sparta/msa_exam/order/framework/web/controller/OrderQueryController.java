package com.sparta.msa_exam.order.framework.web.controller;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.framework.adapter.OrderPersistenceAdapter;
import com.sparta.msa_exam.order.framework.web.dto.OrderReadOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderQueryController {

    private final OrderPersistenceAdapter orderPersistenceAdapter;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{orderId}")
    public OrderReadOutputDTO getOrder(
        @PathVariable(name = "orderId") Long orderId
    ) {
        // TODO : 예외처리 하기
        Order order = orderPersistenceAdapter.findByOrderId(orderId).get();

        return OrderReadOutputDTO.toDTO(order);
    }
}
