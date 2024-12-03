package com.sparta.msa_exam.product.framework.web.controller;

import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.application.domain.ProductForCreate;
import com.sparta.msa_exam.product.application.usecase.ProductUseCase;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateOutputDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductUseCase productUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ProductCreateOutputDTO createProduct(
        @Valid @RequestBody ProductCreateInputDTO productCreateInputDTO,
        @RequestHeader(value = "X-User-Id", required = true) String userId,
        @RequestHeader(value = "X-Role", required = true) String role
    ) {

        ProductForCreate productForCreate = ProductCreateInputDTO.toDomain(
            productCreateInputDTO.name(),
            productCreateInputDTO.supplyPrice(),
            userId,
            role
        );
        Product product = productUseCase.createProduct(productForCreate);

        return ProductCreateOutputDTO.toDTO(product);
    }
}
