package com.sparta.msa_exam.product.framework.web.controller;

import brave.Tracer;
import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.application.domain.ProductForCreate;
import com.sparta.msa_exam.product.application.usecase.ProductUseCase;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateOutputDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductUseCase productUseCase;
    private final Tracer tracer;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ProductCreateOutputDTO createProduct(
        @Valid @RequestBody ProductCreateInputDTO productCreateInputDTO,
        @RequestHeader(value = "X-User-Id", required = true) String userId,
        @RequestHeader(value = "X-Role", required = true) String role
    ) {

        String traceId = tracer.currentSpan() != null
            ? Long.toHexString(tracer.currentSpan().context().traceId())
            : "no-trace-id";
        log.info("Received request to create product with trace ID: {}", traceId);

        ProductForCreate productForCreate = ProductCreateInputDTO.toDomain(
            productCreateInputDTO.name(),
            productCreateInputDTO.supplyPrice(),
            userId,
            role
        );
        Product product = productUseCase.createProduct(productForCreate);

        log.info("Product created with ID: {} and trace ID: {}", product.getId(), traceId);

        return ProductCreateOutputDTO.toDTO(product);
    }
}
