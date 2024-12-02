package com.sparta.msa_exam.order.framework.web.dto;

import com.sparta.msa_exam.order.application.domain.OrderForUpdate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateInputDTO(
    @NotNull(message = "상품ID를 입력해주세요.")
    Long productId,

    @NotNull(message = "수량을 입력해주세요.")
    @Min(value = 1, message = "수량은 1개 이상으로 설정해야 합니다.")
    @Max(value = 100, message = "수량은 100개 이하로 설정해야 합니다.")
    int quantity
) {

    public static OrderForUpdate toDomain(
        Long orderId,
        OrderUpdateInputDTO orderUpdateInputDTO
    ) {
        return new OrderForUpdate(
            orderId,
            orderUpdateInputDTO.productId(),
            orderUpdateInputDTO.quantity()
        );
    }
}
