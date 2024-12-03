package com.sparta.msa_exam.product.application.service;

import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.domain.model.ProductEntity;
import com.sparta.msa_exam.product.framework.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final ProductRepository productRepository;

    @CacheEvict(value = "productCache", allEntries = true, cacheManager = "cacheManager")
    public void evictProductCache() {
        // 상품 저장 시 캐시 갱신 처리
        // 실제 비즈니스 로직을 처리하지 않고 캐시만 삭제
    }

    @Cacheable(value = "productCache", unless = "#result.isEmpty()", cacheManager = "cacheManager")
    public List<Product> findAllProducts() {
        // 상품 목록 조회 시 캐시 적용
        return productRepository.findAll().stream()
            .map(ProductEntity::toDomain)
            .toList();
    }
}