package com.sparta.msa_exam.order.framework.web.controller;

import brave.Tracer;
import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForRead;
import com.sparta.msa_exam.order.application.inputport.OrderInputPort;
import com.sparta.msa_exam.order.framework.web.dto.OrderReadOutputDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderQueryController {

    private final OrderInputPort orderInputPort;
    private final Tracer tracer;

    @Cacheable(value = "orders", key = "#orderId", unless = "#result == null", cacheManager = "cacheManager")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{orderId}")
    public OrderReadOutputDTO getOrder(
        @PathVariable(name = "orderId") Long orderId,
        @RequestHeader(value = "X-User-Id", required = true) String userId,
        @RequestHeader(value = "X-Role", required = true) String role
    ) {

        String traceId = tracer.currentSpan() != null
            ? Long.toHexString(tracer.currentSpan().context().traceId())
            : "no-trace-id";
        log.info("Received request to a read order with trace ID: {}", traceId);

        Long newUserId = Long.parseLong(userId);

        OrderForRead orderForRead = new OrderForRead(
            orderId,
            newUserId
        );
        Order order = orderInputPort.getOrder(orderForRead);

        log.info("Order read with trace ID: {}", traceId);

        return OrderReadOutputDTO.toDTO(order);
    }
}
