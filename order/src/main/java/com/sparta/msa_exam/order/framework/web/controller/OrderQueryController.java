package com.sparta.msa_exam.order.framework.web.controller;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForRead;
import com.sparta.msa_exam.order.application.inputport.OrderInputPort;
import com.sparta.msa_exam.order.framework.web.dto.OrderReadOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderQueryController {

    private final OrderInputPort orderInputPort;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{orderId}")
    public OrderReadOutputDTO getOrder(
        @PathVariable(name = "orderId") Long orderId,
        @RequestHeader(value = "X-User-Id", required = true) String userId,
        @RequestHeader(value = "X-Role", required = true) String role
    ) {

        Long newUserId = Long.parseLong(userId);

        OrderForRead orderForRead = new OrderForRead(
            orderId,
            newUserId
        );
        Order order = orderInputPort.getOrder(orderForRead);

        return OrderReadOutputDTO.toDTO(order);
    }
}
