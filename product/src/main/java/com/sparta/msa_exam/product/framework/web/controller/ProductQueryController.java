package com.sparta.msa_exam.product.framework.web.controller;

import com.sparta.msa_exam.product.domain.model.Product;
import com.sparta.msa_exam.product.framework.adapter.ProductPersistenceAdapter;
import com.sparta.msa_exam.product.framework.web.dto.ProductReadOutputDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class ProductQueryController {

    private final ProductPersistenceAdapter productPersistenceAdapter;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ProductReadOutputDTO.GetProductsResponse getAllProducts(

    ){
        List<Product> findProducts = productPersistenceAdapter.findAllProducts();
        return ProductReadOutputDTO.GetProductsResponse.toDTOList(findProducts);
    }
}
