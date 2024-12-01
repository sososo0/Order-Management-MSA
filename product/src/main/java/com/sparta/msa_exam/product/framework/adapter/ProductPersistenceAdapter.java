package com.sparta.msa_exam.product.framework.adapter;

import com.sparta.msa_exam.product.application.outputport.ProductOutputPort;
import com.sparta.msa_exam.product.domain.model.Product;
import com.sparta.msa_exam.product.framework.repository.ProductRepository;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductPersistenceAdapter implements ProductOutputPort {

    private final ProductRepository productRepository;

    @Override
    public Product saveProduct(ProductCreateInputDTO createInputDTO) {
        return productRepository.save(Product.toEntity(createInputDTO));
    }
}
