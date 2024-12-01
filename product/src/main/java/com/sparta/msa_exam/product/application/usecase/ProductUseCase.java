package com.sparta.msa_exam.product.application.usecase;

import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateOutputDTO;

public interface ProductUseCase {
    ProductCreateOutputDTO createProduct(ProductCreateInputDTO createInputDTO);
}
