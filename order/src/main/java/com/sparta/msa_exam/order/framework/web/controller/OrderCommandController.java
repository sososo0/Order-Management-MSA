package com.sparta.msa_exam.order.framework.web.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderUseCase orderUseCase;

    // TODO: Validated 사용해보기

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderCreateOutputDTO createOrder(
        @Valid @RequestBody OrderCreateInputDTO.OrderProductsInputDTO products,
        @RequestHeader(value = "X-User-Id", required = true) String userId,
        @RequestHeader(value = "X-Role", required = true) String role
    ) {

        OrderForCreate orderForCreate = OrderCreateInputDTO.toDomain(
            products,
            OrderStatus.ORDER_CREATED,
            userId,
            role
        );
        Order order = orderUseCase.createOrder(orderForCreate);

        return OrderCreateOutputDTO.toDTO(order);
    }

    // TODO : fallback api 추가하기 

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{orderId}")
    public OrderUpdateOutputDTO updateOrder(
        @PathVariable(name = "orderId") Long orderId,
        @Valid @RequestBody OrderUpdateInputDTO product
    ) {

        OrderForUpdate orderForUpdate = OrderUpdateInputDTO.toDomain(
            orderId,
            product
        );
        Order order = orderUseCase.updateOrder(orderForUpdate);

        return OrderUpdateOutputDTO.toDTO(order, orderForUpdate.productId());
    }
}
