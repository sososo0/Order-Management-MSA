package com.sparta.msa_exam.order.framework.adapter;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForCreate;
import com.sparta.msa_exam.order.application.domain.OrderForUpdate;
import com.sparta.msa_exam.order.application.outputport.OrderOutputPort;
import com.sparta.msa_exam.order.domain.model.OrderEntity;
import com.sparta.msa_exam.order.domain.model.OrderProductEntity;
import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import com.sparta.msa_exam.order.framework.repository.OrderProductRepository;
import com.sparta.msa_exam.order.framework.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class OrderPersistenceAdapter implements OrderOutputPort {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    // TODO: 지우기
    private static final int TEST_COST = 10_000;

    public Optional<Order> findByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
            .map(OrderEntity::toDomain)
            .or(Optional::empty);
    }

    @Transactional
    @Override
    public Order saveOrder(OrderForCreate orderForCreate) {

        // TODO: Product 찾고, 가격 계산 로직 추가

        OrderEntity orderEntity = orderRepository.save(
            OrderEntity.toEntity(orderForCreate.status(), TEST_COST));

        try {
            List<OrderProductEntity> orderProductEntities = orderForCreate.products().stream()
                .map(orderProductForCreate -> {
                    // TODO: Product 조회

                    return OrderProductEntity.toEntity(
                        orderEntity,
                        orderProductForCreate.productId(),
                        orderProductForCreate.quantity()
                    );
                }).collect(Collectors.toList());
            orderProductRepository.saveAll(orderProductEntities);

            orderProductEntities.forEach(orderEntity::updateProductIds);

            orderEntity.updateOrderStatus(OrderStatus.ORDER_SUCCESS);

        } catch (Exception e) { // TODO: 예외처리 분리시키기
            orderEntity.updateOrderStatus(OrderStatus.ORDER_FAIL);
            throw new RuntimeException("주문 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

        return orderEntity.toDomain();
    }

    @Transactional
    @Override
    public Order updateOrder(OrderForUpdate orderForUpdate) {

        // TODO : 예외처리 하기
        OrderEntity orderEntity = orderRepository.findById(orderForUpdate.orderId()).get();

        try {
            Optional<OrderProductEntity> existingOrderProduct = orderEntity.getProductIds().stream()
                .filter(product -> product.getProductId().equals(orderForUpdate.productId()))
                .findFirst();

            if (existingOrderProduct.isPresent()) {
                OrderProductEntity orderProductEntity = existingOrderProduct.get();
                orderProductEntity.updateQuantity(orderForUpdate.quantity());
                orderProductRepository.save(orderProductEntity);
            } else {
                OrderProductEntity orderProductEntity = orderProductRepository.save(
                    OrderProductEntity.toEntity(orderEntity, orderForUpdate.productId(),
                        orderForUpdate.quantity())
                );
                orderEntity.updateProductIds(orderProductEntity);
            }

        } catch (Exception e) { // TODO: 예외처리 분리시키기
            orderEntity.updateOrderStatus(OrderStatus.ORDER_FAIL);
            throw new RuntimeException("예외처리");
        }

        return orderEntity.toDomain();
    }
}
