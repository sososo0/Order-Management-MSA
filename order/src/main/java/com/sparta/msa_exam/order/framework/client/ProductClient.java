package com.sparta.msa_exam.order.framework.client;

import com.sparta.msa_exam.order.framework.web.dto.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product", fallbackFactory = ProductClientFallbackFactory.class)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductResponseDTO getProduct(@PathVariable("id") Long id);
}