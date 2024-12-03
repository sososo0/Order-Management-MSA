package com.sparta.msa_exam.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomPrefilter implements GlobalFilter, Ordered {

    // TODO : 요청 캐싱 또는 중복 요청 방지하기, 로깅 및 메트리 수집하기

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Pre Filter: Request URI is {}", request.getURI());

        String path = request.getPath().toString();

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).doFinally(signalType -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info("{}에 대한 요청이 {} ms 초에 처리되었습니다.", path, duration);
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
