package com.sparta.msa_exam.product.application.inputport;

import com.sparta.msa_exam.product.application.outputport.ProductOutputPort;
import com.sparta.msa_exam.product.application.usecase.ProductUseCase;
import com.sparta.msa_exam.product.domain.model.Product;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInputPort implements ProductUseCase {

    private final ProductOutputPort productOutputPort;

    @Override
    public ProductCreateOutputDTO createProduct(ProductCreateInputDTO createInputDTO) {

        Product createProduct = productOutputPort.saveProduct(createInputDTO);

        return ProductCreateOutputDTO.toDTO(createProduct);
    }
}
