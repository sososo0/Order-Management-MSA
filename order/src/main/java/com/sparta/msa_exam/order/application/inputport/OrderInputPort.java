package com.sparta.msa_exam.order.application.inputport;

import com.sparta.common.exception.CustomException;
import com.sparta.common.exception.ErrorCode;
import com.sparta.common.exception.OrderProcessingException;
import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderForRead;
import com.sparta.msa_exam.order.application.domain.OrderForUpdate;
import com.sparta.msa_exam.order.application.outputport.OrderOutputPort;
import com.sparta.msa_exam.order.application.usecase.OrderUseCase;
import com.sparta.msa_exam.order.framework.client.ProductClient;
import com.sparta.msa_exam.order.framework.web.dto.ProductResponseDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderInputPort implements OrderUseCase {

    private final OrderOutputPort orderOutputPort;
    private final ProductClient productClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostConstruct
    public void registerEventListener() {
        circuitBreakerRegistry.circuitBreaker("productService").getEventPublisher()
            .onStateTransition(event -> log.info("####### CircuitBreaker State Transition: {}", event))
            .onFailureRateExceeded(event -> log.info("####### CircuitBreaker Failure Rate Exceeded: {}", event))
            .onCallNotPermitted(event -> log.info("####### CircuitBreaker Call Not Permitted: {}", event))
            .onError(event -> log.info("####### CircuitBreaker Error: {}", event));
    }

    @Override
    public Order getOrder(OrderForRead orderForRead) {
        Order order = orderOutputPort.findByOrderId(orderForRead.orderId()).orElseThrow(
            () -> new CustomException(
                ErrorCode.ORDER_NOT_EXIST.getCode(),
                ErrorCode.ORDER_NOT_EXIST.getDescription(),
                ErrorCode.ORDER_NOT_EXIST.getDetailMessage()
            )
        );

        if (!orderForRead.userId().equals(order.getUserId())) {
            throw new CustomException(
                ErrorCode.ORDER_ACCESS_DENIED.getCode(),
                ErrorCode.ORDER_ACCESS_DENIED.getDescription(),
                ErrorCode.ORDER_NOT_EXIST.getDetailMessage()
            );
        }
        return order;
    }

    @Transactional
    @Override
    public Order createOrder(OrderForCreate orderForCreate) {
        int totalCost = calculateTotalCost(orderForCreate);
        return orderOutputPort.saveOrder(orderForCreate, totalCost);
    }

    @Transactional
    @Override
    public Order updateOrder(OrderForUpdate orderForUpdate) {
        Order order = validateOrder(orderForUpdate.orderId());
        if (!order.getUserId().equals(orderForUpdate.userId())) {
            throw new CustomException(
                ErrorCode.ORDER_ACCESS_DENIED.getCode(),
                ErrorCode.ORDER_ACCESS_DENIED.getDescription(),
                ErrorCode.ORDER_ACCESS_DENIED.getDetailMessage()
            );
        }
        getProductDetailsWithCircuitBreaker(orderForUpdate.productId());
        return orderOutputPort.updateOrder(orderForUpdate);
    }

    @Override
    public void failOrder(Long productId) {
        getProductDetailsWithCircuitBreaker(productId);
    }

    private int calculateTotalCost(OrderForCreate orderForCreate) {
        return orderForCreate.products().stream()
            .mapToInt(product -> {
                try {
                    ProductResponseDTO productResponseDTO = getProductDetailsWithCircuitBreaker(product.productId());
                    log.info("Product ID: {}, Quantity: {}, Supply Price: {}",
                             productResponseDTO.productId(), product.quantity(), productResponseDTO.supplyPrice());
                    return productResponseDTO.supplyPrice() * product.quantity();
                } catch (OrderProcessingException e) {
                    log.error("Error fetching product details for productId: {}: {}", product.productId(), e.getMessage(), e);
                    throw new OrderProcessingException("잠시 후에 주문 추가를 요청 해주세요.");
                }
            })
            .sum();
    }

    private Order validateOrder(Long orderId) {
        return orderOutputPort.findByOrderId(orderId).orElseThrow(
            () -> new CustomException(
                ErrorCode.ORDER_NOT_EXIST.getCode(),
                ErrorCode.ORDER_NOT_EXIST.getDescription(),
                ErrorCode.ORDER_NOT_EXIST.getDetailMessage()
            )
        );
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProductDetails")
    private ProductResponseDTO getProductDetailsWithCircuitBreaker(Long productId) {
        try {
            return productClient.getProduct(productId);
        } catch (FeignException e) {
            log.error("FeignException occurred, throwing custom exception.");
            throw new OrderProcessingException("잠시 후에 주문 추가를 요청 해주세요.");
        }
    }

    private ProductResponseDTO fallbackGetProductDetails(Long productId, Throwable t) {
        log.error("#### Fallback triggered for productId: {} due to: {}", productId, t.getMessage());
        throw new OrderProcessingException("잠시 후에 주문 추가를 요청 해주세요.");
    }
}
