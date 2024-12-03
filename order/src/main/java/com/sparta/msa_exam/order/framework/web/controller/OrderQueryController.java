package com.sparta.msa_exam.order.framework.web.controller;

import brave.Tracer;
import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderForRead;
import com.sparta.msa_exam.order.application.inputport.OrderInputPort;
import com.sparta.msa_exam.order.framework.web.dto.OrderReadOutputDTO;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderQueryController {

    private final OrderInputPort orderInputPort;
    private final Tracer tracer;
    RedisTemplate<String, OrderReadOutputDTO> orderReadTemplate;

    private static final long CACHE_EXPIRATION_TIME = 60;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{orderId}")
    public OrderReadOutputDTO getOrder(
        @PathVariable(name = "orderId") Long orderId,
        @RequestHeader(value = "X-User-Id", required = true) String userId,
        @RequestHeader(value = "X-Role", required = true) String role
    ) {

        String traceId = tracer.currentSpan() != null
            ? Long.toHexString(tracer.currentSpan().context().traceId())
            : "no-trace-id";
        log.info("Received request to a read order with trace ID: {}", traceId);

        Long newUserId = Long.parseLong(userId);

        // 캐시 키 설정
//        String cacheKey = "order::" + orderId;
//
//        // 캐시에서 조회
//        OrderReadOutputDTO cachedOrder = orderReadTemplate.opsForValue().get(cacheKey);

//        if (cachedOrder != null) {
//            log.info("Cache hit for orderId: {} with trace ID: {}", orderId, traceId);
//            return cachedOrder; // 캐시가 있으면 캐시된 데이터 반환
//        }

        // 캐시 미스 시 DB에서 조회
        OrderForRead orderForRead = new OrderForRead(
            orderId,
            newUserId
        );
        Order order = orderInputPort.getOrder(orderForRead);

        log.info("Order read with trace ID: {}", traceId);

        // 조회된 주문 데이터를 DTO로 변환하여 캐시에 저장
        OrderReadOutputDTO orderDto = OrderReadOutputDTO.toDTO(order);

        // 캐시 저장 (60초 동안 유지)
//        orderReadTemplate.opsForValue().set(cacheKey, orderDto, CACHE_EXPIRATION_TIME, TimeUnit.SECONDS);

        return orderDto;
    }
}
