package com.sparta.msa_exam.product.application.inputport;

import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.application.domain.ProductForCreate;
import com.sparta.msa_exam.product.application.outputport.ProductOutputPort;
import com.sparta.msa_exam.product.application.usecase.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductInputPort implements ProductUseCase {

    private final ProductOutputPort productOutputPort;

    @Override
    public Product createProduct(ProductForCreate productForCreate) {

        if (!productForCreate.userRole().equals("OWNER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User role is not OWNER.");
        }

        return productOutputPort.saveProduct(productForCreate);
    }
}
