package com.sparta.msa_exam.product.framework.web.dto;

import com.sparta.msa_exam.product.application.domain.ProductForCreate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductCreateInputDTO(
    @NotBlank(message = "상품명을 입력해주세요.")
    String name,

    @NotNull(message = "가격을 입력해주세요.")
    @Positive(message = "가격은 0원 이상으로 설정해야 합니다.")
    @Max(value = 10_000_000, message = "가격은 10_000_000 이하로 설정해야 합니다.")
    Integer supplyPrice
) {

    public static ProductForCreate toDomain(
        String name,
        Integer supplyPrice
    ) {
        return new ProductForCreate(
            name,
            supplyPrice
        );
    }
}
