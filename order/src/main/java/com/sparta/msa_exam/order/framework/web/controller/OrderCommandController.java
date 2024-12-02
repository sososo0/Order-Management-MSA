package com.sparta.msa_exam.order.framework.web.controller;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.usecase.OrderUseCase;
import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import com.sparta.msa_exam.order.framework.web.dto.OrderCreateOutputDTO;
import com.sparta.msa_exam.order.framework.web.dto.OrderInputDTO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderUseCase orderUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderCreateOutputDTO createOrder(
        @Valid @RequestBody OrderInputDTO.OrderCreateInputDTO products
    ) {

        OrderForCreate orderForCreate = OrderInputDTO.toDomain(
            products,
            OrderStatus.ORDER_CREATED
        );
        Order order = orderUseCase.createOrder(orderForCreate);

        return OrderCreateOutputDTO.toDTO(order);
    }
}
