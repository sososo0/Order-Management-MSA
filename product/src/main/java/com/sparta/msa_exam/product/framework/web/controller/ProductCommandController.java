package com.sparta.msa_exam.product.framework.web.controller;

import com.sparta.msa_exam.product.application.usecase.ProductUseCase;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateOutputDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductUseCase productUseCase;

    @PostMapping
    public ProductCreateOutputDTO createProduct(
        @Valid @RequestBody ProductCreateInputDTO productCreateInputDTO
    ) {
        return productUseCase.createProduct(productCreateInputDTO);
    }
}
