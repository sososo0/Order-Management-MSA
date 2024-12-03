package com.sparta.msa_exam.gateway;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomPostFilter implements GlobalFilter, Ordered {

    private final ReactiveDiscoveryClient discoveryClient;

    private final Map<String, String> moduleMapping = Map.of(
        "auth", "auth",
        "order", "order",
        "product", "product"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.defer(() -> {
            String path = exchange.getRequest().getPath().toString();
            String moduleName = extractModuleName(path);

            log.info("요청 path: {}", path);
            log.info("module 명: {}", moduleName);

            return getPortFromDiscoveryClient(moduleName)
                    .doOnNext(port -> log.info("module {}: 포트 {}", moduleName, port)) // 포트 로깅
                    .flatMap(port -> {
                        exchange.getResponse().getHeaders().add("Server-Port", port);
                        log.info("Server-Port header: {}", port); // 헤더 추가 로깅
                        return Mono.empty();
                    });
        }));
    }

    private String extractModuleName(String path) {
        String moduleName = path.split("/")[2];
        return moduleMapping.getOrDefault(moduleName, "unknown");
    }

    private Mono<String> getPortFromDiscoveryClient(String moduleName) {
        return discoveryClient.getInstances(moduleName)
            .next() // 첫 번째 인스턴스 가져오기
            .map(serviceInstance -> String.valueOf(serviceInstance.getPort()))
            .defaultIfEmpty("unknown");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
