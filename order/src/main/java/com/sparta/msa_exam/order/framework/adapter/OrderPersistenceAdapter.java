package com.sparta.msa_exam.order.framework.adapter;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderForUpdate;
import com.sparta.msa_exam.order.application.outputport.OrderOutputPort;
import com.sparta.msa_exam.order.domain.model.OrderEntity;
import com.sparta.msa_exam.order.domain.model.OrderProductEntity;
import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import com.sparta.msa_exam.order.framework.client.ProductClient;
import com.sparta.msa_exam.order.framework.repository.OrderProductRepository;
import com.sparta.msa_exam.order.framework.repository.OrderRepository;
import com.sparta.msa_exam.order.framework.web.dto.ProductResponseDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderPersistenceAdapter implements OrderOutputPort {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
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

    public Optional<Order> findByOrderIdAndUserId(Long orderId, Long userId) {
        return orderRepository.findByOrderIdAndUserId(orderId, userId)
            .map(OrderEntity::toDomain)
            .or(Optional::empty);
    }

    @Transactional
    @Override
    public Order saveOrder(OrderForCreate orderForCreate) {
        try {
            int totalCost = calculateTotalCost(orderForCreate);
            log.info("Calculated total cost: {}", totalCost);

            OrderEntity orderEntity = orderRepository.save(
                OrderEntity.toEntity(orderForCreate.status(), totalCost, orderForCreate.userId())
            );
            log.info("Order saved with ID: {}", orderEntity.getId());

            List<OrderProductEntity> orderProductEntities = orderForCreate.products().stream()
                .map(orderProductForCreate -> {
                    return OrderProductEntity.toEntity(
                        orderEntity,
                        orderProductForCreate.productId(),
                        orderProductForCreate.quantity()
                    );
                }).collect(Collectors.toList());
            log.info("Order products saved: {}", orderProductEntities.size());

            orderProductRepository.saveAll(orderProductEntities);

            orderProductEntities.forEach(orderEntity::setProductIds);

            orderEntity.updateOrderStatus(OrderStatus.ORDER_SUCCESS);

            return orderEntity.toDomain();

        } catch (DataIntegrityViolationException e) {
                log.error("Database error: {}", e.getMessage(), e);
                throw new RuntimeException("Database error during order processing", e);

        } catch (Exception e) { // TODO: 예외처리 분리시키기
            throw e;
        }
    }

    private int calculateTotalCost(OrderForCreate orderForCreate) {
        return orderForCreate.products().stream()
            .mapToInt(product -> {
                try {
                    ProductResponseDTO productResponseDTO = getProductDetailsWithCircuitBreaker(product.productId());
                    log.info("Product ID: {}, Quantity: {}, Supply Price: {}",
                             productResponseDTO.productId(), product.quantity(), productResponseDTO.supplyPrice());
                    return productResponseDTO.supplyPrice() * product.quantity();
                } catch (RuntimeException e) {
                    log.error("Error fetching product details: {}", e.getMessage(), e);
                    throw new RuntimeException("주문 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                }
            })
            .sum();
    }

    @Transactional
    @Override
    public Order updateOrder(OrderForUpdate orderForUpdate) {
        try {
            // TODO : 예외처리 하기
            OrderEntity orderEntity = orderRepository.findById(orderForUpdate.orderId()).get();

            getProductDetailsWithCircuitBreaker(orderForUpdate.productId());

            updateOrAddOrderProduct(orderEntity, orderForUpdate);

            return orderEntity.toDomain();
        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage(), e);
            throw e; // 클라이언트 오류
        } catch (Exception e) { // TODO: 예외처리 분리시키기
            throw new RuntimeException("예외처리");
        }
    }

    private void updateOrAddOrderProduct(OrderEntity orderEntity, OrderForUpdate orderForUpdate) {
        Optional<OrderProductEntity> existingOrderProduct = orderEntity.getProductIds().stream()
            .filter(product -> product.getProductId().equals(orderForUpdate.productId()))
            .findFirst();

        if (existingOrderProduct.isPresent()) {
            // 기존 OrderProduct 업데이트
            OrderProductEntity orderProductEntity = existingOrderProduct.get();
            orderProductEntity.updateQuantity(orderForUpdate.quantity());
            orderProductRepository.save(orderProductEntity);
            log.info("Updated OrderProduct: {}", orderProductEntity);
        } else {
            // 새로운 OrderProduct 추가
            OrderProductEntity orderProductEntity = orderProductRepository.save(
                OrderProductEntity.toEntity(orderEntity, orderForUpdate.productId(), orderForUpdate.quantity())
            );
            orderEntity.updateProductIds(orderProductEntity, orderForUpdate.userId());
            log.info("Added new OrderProduct: {}", orderProductEntity);
        }
    }

    @CircuitBreaker(name = "OrderPersistenceAdapter", fallbackMethod = "fallbackGetProductDetails")
    private ProductResponseDTO getProductDetailsWithCircuitBreaker(Long productId) {
        return productClient.getProduct(productId);
    }

    private ProductResponseDTO fallbackGetProductDetails(Long productId, Throwable t) {
        log.error("#### Fallback triggered for productId: {} due to: {}", productId, t.getMessage());
        return new ProductResponseDTO(productId, "test",0); // 기본 공급 가격을 0으로 설정
    }
}
