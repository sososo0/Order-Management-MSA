package com.sparta.msa_exam.order.framework.web.dto;

public record ProductResponseDTO(
    Long productId,
    String name,
    Integer supplyPrice
) {

}
