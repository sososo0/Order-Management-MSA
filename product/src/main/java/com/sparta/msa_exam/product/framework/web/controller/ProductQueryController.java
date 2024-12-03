package com.sparta.msa_exam.product.framework.web.controller;

import brave.Tracer;
import com.sparta.common.exception.CustomException;
import com.sparta.common.exception.ErrorCode;
import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.framework.adapter.ProductPersistenceAdapter;
import com.sparta.msa_exam.product.framework.web.dto.ProductReadOutputDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class ProductQueryController {

    private final ProductPersistenceAdapter productPersistenceAdapter;
    private final Tracer tracer;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ProductReadOutputDTO.GetProductsResponse getAllProducts(){

        String traceId = tracer.currentSpan() != null
            ? Long.toHexString(tracer.currentSpan().context().traceId())
            : "no-trace-id";
        log.info("Received request to get products with trace ID: {}", traceId);

        List<Product> findProducts = productPersistenceAdapter.findAllProducts();

        log.info("Products read with trace ID: {}", traceId);

        return ProductReadOutputDTO.GetProductsResponse.toDTOList(findProducts);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{productId}")
    public ProductReadOutputDTO getProduct(
        @PathVariable(value = "productId") Long productId
    ){

        String traceId = tracer.currentSpan() != null
            ? Long.toHexString(tracer.currentSpan().context().traceId())
            : "no-trace-id";
        log.info("Received request to a get product with trace ID: {}", traceId);

        Product findProduct = productPersistenceAdapter.findByProductId(productId).orElseThrow(
            () -> new CustomException(
                ErrorCode.PRODUCT_NOT_EXIST.getCode(),
                ErrorCode.PRODUCT_NOT_EXIST.getDescription(),
                ErrorCode.PRODUCT_NOT_EXIST.getDetailMessage()
            )
        );

        log.info("Products read with product ID: {} trace ID: {}", productId, traceId);

        return ProductReadOutputDTO.toDTO(findProduct);
    }
}
