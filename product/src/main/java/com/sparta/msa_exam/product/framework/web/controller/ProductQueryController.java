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
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, ProductReadOutputDTO.GetProductsResponse> productGetTemplate;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ProductReadOutputDTO.GetProductsResponse getAllProducts(){

        String traceId = tracer.currentSpan() != null
            ? Long.toHexString(tracer.currentSpan().context().traceId())
            : "no-trace-id";
        log.info("Received request to get products with trace ID: {}", traceId);

        // 캐시에서 상품 목록 조회
//        ProductReadOutputDTO.GetProductsResponse cachedProducts = productGetTemplate.opsForValue().get("productCache::all");
//
//        if (cachedProducts != null) {
//            log.info("Cache hit for product list with trace ID: {}", traceId);
//            return cachedProducts;
//        }

        List<Product> findProducts = productPersistenceAdapter.findAllProducts();
        log.info("Products read from DB with trace ID: {}", traceId);

        // DB에서 조회한 상품 목록을 DTO로 변환
        ProductReadOutputDTO.GetProductsResponse productResponse = ProductReadOutputDTO.GetProductsResponse.toDTOList(findProducts);

        // 상품 목록을 캐시 저장
//        productGetTemplate.opsForValue().set("productCache::all", productResponse);

        return productResponse;
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
