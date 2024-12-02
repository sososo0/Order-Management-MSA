package com.sparta.msa_exam.product.framework.web.dto;

import com.sparta.msa_exam.product.application.domain.Product;

public record ProductCreateOutputDTO(
    Long productId,
    String name,
    Integer supplyPrice
) {

    public static ProductCreateOutputDTO toDTO(Product product) {
        return new ProductCreateOutputDTO(
            product.getId(),
            product.getName(),
            product.getSupplyPrice()
        );
    }
}
