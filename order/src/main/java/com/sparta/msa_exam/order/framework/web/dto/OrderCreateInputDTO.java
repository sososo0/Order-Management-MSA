package com.sparta.msa_exam.order.framework.web.dto;

import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderProductForCreate;
import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public record OrderCreateInputDTO(
    @NotNull(message = "상품ID를 입력해주세요.")
    Long productId,

    @NotNull(message = "수량을 입력해주세요.")
    @Min(value = 1, message = "수량은 1개 이상으로 설정해야 합니다.")
    @Max(value = 100, message = "수량은 100개 이하로 설정해야 합니다.")
    int quantity
) {

    public record OrderProductsInputDTO(
        @NotNull(message = "상품 리스트를 입력해주세요.")
        @Valid
        List<@Valid OrderCreateInputDTO> products
    ) {

    }

    public static OrderForCreate toDomain(
        OrderProductsInputDTO productsRequest,
        OrderStatus orderStatus,
        String stringUserId,
        String role
    ) {

        Long userId = Long.parseLong(stringUserId);

        List<OrderProductForCreate> orderProducts = productsRequest.products().stream()
            .map(product -> new OrderProductForCreate(product.productId(), product.quantity()))
            .collect(Collectors.toList());

        return new OrderForCreate(
            orderProducts,
            orderStatus,
            userId,
            role
        );
    }
}
