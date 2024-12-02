package com.sparta.msa_exam.product.application.domain;

import lombok.Getter;

@Getter
public class Product {
    private Long id;
    private String name;
    private Integer supplyPrice;

    public Product(
        Long id,
        String name,
        Integer supplyPrice
    ) {
        this.id = id;
        this.name = name;
        this.supplyPrice = supplyPrice;
    }
}
