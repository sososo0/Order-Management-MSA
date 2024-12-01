package com.sparta.msa_exam.product.framework.web.dto;

import com.sparta.msa_exam.product.domain.model.Product;

public record ProductCreateOutputDTO(
    Long productId,
    String name,
    Integer supplyPrice
) {

    public static ProductCreateOutputDTO toDTO(Product createProduct) {
        return new ProductCreateOutputDTO(
            createProduct.getId(),
            createProduct.getName(),
            createProduct.getSupplyPrice()
        );
    }
}
