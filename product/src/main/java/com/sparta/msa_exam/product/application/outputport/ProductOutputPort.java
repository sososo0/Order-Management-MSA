package com.sparta.msa_exam.product.application.outputport;

import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.application.domain.ProductForCreate;
import java.util.List;

public interface ProductOutputPort {
    Product saveProduct(ProductForCreate productForCreate);
    List<Product> findAllProducts();
}
