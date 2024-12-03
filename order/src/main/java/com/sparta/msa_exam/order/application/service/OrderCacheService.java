package com.sparta.msa_exam.order.application.service;

import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.domain.model.OrderEntity;
import com.sparta.msa_exam.order.framework.repository.OrderRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCacheService {

    private final OrderRepository orderRepository;

    @Cacheable(value = "orderCache", key = "#orderForRead.orderId()", unless = "#result.empty", cacheManager = "cacheManager")
    public Optional<Order> findByOrderId(Long orderId) {
        return orderRepository.findById(orderId).map(OrderEntity::toDomain).or(Optional::empty);
    }
}