package com.sparta.msa_exam.product.framework.config;

import com.sparta.msa_exam.product.framework.web.dto.ProductCreateOutputDTO;
import com.sparta.msa_exam.product.framework.web.dto.ProductReadOutputDTO;
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
    public RedisTemplate<String, ProductCreateOutputDTO> productCreateTemplate(
        RedisConnectionFactory redisConnectionFactory
    ) {
        RedisTemplate<String, ProductCreateOutputDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        return template;
    }

    @Bean
    public RedisTemplate<String, ProductReadOutputDTO.GetProductsResponse> productGetTemplate(
        RedisConnectionFactory redisConnectionFactory
    ) {
        RedisTemplate<String, ProductReadOutputDTO.GetProductsResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        return template;
    }
}
