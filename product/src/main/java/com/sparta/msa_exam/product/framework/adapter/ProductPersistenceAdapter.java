package com.sparta.msa_exam.product.framework.adapter;

import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.application.domain.ProductForCreate;
import com.sparta.msa_exam.product.application.outputport.ProductOutputPort;
import com.sparta.msa_exam.product.domain.model.ProductEntity;
import com.sparta.msa_exam.product.framework.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductPersistenceAdapter implements ProductOutputPort {

    private final ProductRepository productRepository;

    public Optional<Product> findByProductId(Long productId) {
        return productRepository.findByProductId(productId)
            .map(ProductEntity::toDomain)
            .or(Optional::empty);
    }

    @Override
    public Product saveProduct(ProductForCreate productForCreate) {
        return productRepository.save(ProductEntity.toEntity(productForCreate)).toDomain();
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll().stream()
            .map(ProductEntity::toDomain)
            .toList();
    }
}
