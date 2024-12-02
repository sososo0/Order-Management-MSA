package com.sparta.msa_exam.product.application.usecase;

import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.application.domain.ProductForCreate;

public interface ProductUseCase {
    Product createProduct(ProductForCreate productForCreate);
}
