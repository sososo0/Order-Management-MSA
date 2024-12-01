package com.sparta.msa_exam.product.framework.adapter;

import com.sparta.msa_exam.product.application.outputport.ProductOutputPort;
import com.sparta.msa_exam.product.domain.model.Product;
import com.sparta.msa_exam.product.framework.repository.ProductRepository;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
import java.util.List;
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

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
}
