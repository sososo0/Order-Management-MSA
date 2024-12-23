package com.sparta.msa_exam.order.framework.web.controller;

import brave.Tracer;
import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderForUpdate;
import com.sparta.msa_exam.order.application.usecase.OrderUseCase;
import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import com.sparta.msa_exam.order.framework.web.dto.OrderCreateOutputDTO;
import com.sparta.msa_exam.order.framework.web.dto.OrderCreateInputDTO;
import com.sparta.msa_exam.order.framework.web.dto.OrderUpdateInputDTO;
import com.sparta.msa_exam.order.framework.web.dto.OrderUpdateOutputDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderUseCase orderUseCase;
    private final Tracer tracer;

    // TODO: Validated 사용해보기

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderCreateOutputDTO createOrder(
        @Valid @RequestBody OrderCreateInputDTO.OrderProductsInputDTO products,
        @RequestHeader(value = "X-User-Id", required = true) String userId,
        @RequestHeader(value = "X-Role", required = true) String role,
        @RequestParam(value = "fail", required = false, defaultValue = "false") boolean fail
    ) {

        String traceId = tracer.currentSpan() != null
            ? Long.toHexString(tracer.currentSpan().context().traceId())
            : "no-trace-id";
        log.info("Received request to a create order with trace ID: {}", traceId);

        if (fail) {
            log.info("상품 API 호출 실패 케이스");
            orderUseCase.failOrder(0L);
        }

        OrderForCreate orderForCreate = OrderCreateInputDTO.toDomain(
            products,
            OrderStatus.ORDER_CREATED,
            userId,
            role
        );
        Order order = orderUseCase.createOrder(orderForCreate);

        log.info("Order create with trace ID: {}", traceId);

        return OrderCreateOutputDTO.toDTO(order);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{orderId}")
    public OrderUpdateOutputDTO updateOrder(
        @PathVariable(name = "orderId") Long orderId,
        @Valid @RequestBody OrderUpdateInputDTO product,
        @RequestHeader(value = "X-User-Id", required = true) String userId,
        @RequestHeader(value = "X-Role", required = true) String role
    ) {

        String traceId = tracer.currentSpan() != null
            ? Long.toHexString(tracer.currentSpan().context().traceId())
            : "no-trace-id";
        log.info("Received request to a update order with trace ID: {}", traceId);

        OrderForUpdate orderForUpdate = OrderUpdateInputDTO.toDomain(
            orderId,
            product,
            userId,
            role
        );
        Order order = orderUseCase.updateOrder(orderForUpdate);

        log.info("Order update with trace ID: {}", traceId);

        return OrderUpdateOutputDTO.toDTO(order, orderForUpdate.productId());
    }
}
