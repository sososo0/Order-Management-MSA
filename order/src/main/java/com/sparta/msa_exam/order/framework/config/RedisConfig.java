package com.sparta.msa_exam.order.framework.config;

import com.sparta.msa_exam.order.framework.web.dto.OrderReadOutputDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String HOST;

    @Value("${redis.port}")
    private int PORT;

    @Value("${redis.password}")
    private String PASSWORD;

    @Bean
    public RedisTemplate<String, OrderReadOutputDTO> orderReadTemplate (
        RedisConnectionFactory redisConnectionFactory
    ) {
        RedisTemplate<String, OrderReadOutputDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        return template;
    }
}
