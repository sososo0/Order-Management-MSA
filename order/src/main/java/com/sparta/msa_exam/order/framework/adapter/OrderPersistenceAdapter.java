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
import jakarta.ws.rs.NotFoundException;
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


    // TODO: 지우기
    private static final Long TEST_USER_ID = 1L;

    public Optional<Order> findByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
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
            throw new RuntimeException("주문 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private int calculateTotalCost(OrderForCreate orderForCreate) {
        return orderForCreate.products().stream()
            .mapToInt(product -> {
                try {
                    ProductResponseDTO productResponseDTO = productClient.getProduct(product.productId());
                    if (productResponseDTO == null) {
                        throw new NotFoundException("상품 ID " + product.productId() + "가 존재하지 않습니다.");
                    }
                    log.info("Product ID: {}", productResponseDTO.productId());
                    return productResponseDTO.supplyPrice() * product.quantity();
                } catch (FeignException e) {
                    log.error("Product service 호출 중 오류: {}", e.getMessage());
                    throw new RuntimeException("상품 정보를 가져오는 중 오류가 발생했습니다.");
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

            validateProductExists(orderForUpdate.productId());

            updateOrAddOrderProduct(orderEntity, orderForUpdate);

            return orderEntity.toDomain();
        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage(), e);
            throw e; // 클라이언트 오류
        } catch (FeignException e) {
            log.error("Product service 호출 중 오류: {}", e.getMessage(), e);
            throw new RuntimeException("상품 정보를 가져오는 중 오류가 발생했습니다.", e);
        } catch (Exception e) { // TODO: 예외처리 분리시키기
            throw new RuntimeException("예외처리");
        }
    }

    private void validateProductExists(Long productId) {
        try {
            productClient.getProduct(productId);
        } catch (FeignException.NotFound e) {
            log.error("Product not found: ID={}", productId);
            throw new IllegalArgumentException("상품 ID " + productId + "가 존재하지 않습니다.");
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
}
