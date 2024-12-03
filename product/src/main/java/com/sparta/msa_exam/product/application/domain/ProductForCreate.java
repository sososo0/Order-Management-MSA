package com.sparta.msa_exam.product.application.domain;

public record ProductForCreate(
    String name,
    Integer supplyPrice,
    Long userId,
    String userRole
) {

}
