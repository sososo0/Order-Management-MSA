package com.sparta.msa_exam.product.application.inputport;

import com.sparta.common.exception.CustomException;
import com.sparta.common.exception.ErrorCode;
import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.application.domain.ProductForCreate;
import com.sparta.msa_exam.product.application.outputport.ProductOutputPort;
import com.sparta.msa_exam.product.application.usecase.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInputPort implements ProductUseCase {

    private final ProductOutputPort productOutputPort;

    @Override
    public Product createProduct(ProductForCreate productForCreate) {

        if (!productForCreate.userRole().equals("OWNER")) {
            throw new CustomException(
                ErrorCode.FORBIDDEN.getCode(),
                ErrorCode.FORBIDDEN.getDescription(),
                ErrorCode.FORBIDDEN.getDetailMessage()
            );
        }

        return productOutputPort.saveProduct(productForCreate);
    }
}
