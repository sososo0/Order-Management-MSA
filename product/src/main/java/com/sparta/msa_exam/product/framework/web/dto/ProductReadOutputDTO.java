package com.sparta.msa_exam.product.framework.web.dto;

import com.sparta.msa_exam.product.domain.model.Product;
import java.util.List;
import java.util.stream.Collectors;

public record ProductReadOutputDTO(
    Long productId,
    String name,
    Integer supplyPrice
) {

    public static ProductReadOutputDTO toDTO(Product product) {
        return new ProductReadOutputDTO(
            product.getId(),
            product.getName(),
            product.getSupplyPrice()
        );
    }

    public record GetProductsResponse(
        List<ProductReadOutputDTO> products
    ) {
        public static GetProductsResponse toDTOList(List<Product> products) {
            List<ProductReadOutputDTO> productDTOList = products.stream()
                .map(ProductReadOutputDTO::toDTO)
                .collect(Collectors.toList());
            return new GetProductsResponse(productDTOList);
        }
    }
}
