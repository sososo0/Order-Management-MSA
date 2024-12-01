package com.sparta.msa_exam.product.application.outputport;

import com.sparta.msa_exam.product.domain.model.Product;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
import java.util.List;

public interface ProductOutputPort {
    Product saveProduct(ProductCreateInputDTO createInputDTO);
    List<Product> findAllProducts();
}
