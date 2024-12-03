package com.sparta.msa_exam.order.framework.client;

import com.sparta.common.exception.OrderProcessingException;
import com.sparta.msa_exam.order.framework.web.dto.ProductResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public ProductResponseDTO getProduct(Long id) {
                log.error("ProductService 호출 실패, 원인: {}", cause.getMessage());

                // 예외를 던져서 상위 로직에서 처리하도록 함
                throw new OrderProcessingException("잠시 후에 주문 추가를 요청 해주세요.");
            }
        };
    }
}
