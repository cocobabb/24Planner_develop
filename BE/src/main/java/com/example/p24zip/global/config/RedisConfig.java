package com.example.p24zip.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Redis MessageBroker 설정
     *
     * @param connectionFactory : Redis와 실제로 연결할 수 있도록, 연결 정보를 가진 팩토리를 주입
     * @return RedisMessageListenerContainer
     * @apiNote container.setConnectionFactory(connectionFactory) : Redis와 실제로 통신할 수 있도록 연결 정보를 가진
     * 팩토리를 주입 정보를 가진 connectionFactory를 컨테이너에 주입
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory connectionFactory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    /**
     * Key는 문자열, Value는 JSON(커스텀 ObjectMapper)으로 직렬화하여 Redis에 저장/조회할 수 있는 RedisTemplate을 Bean으로 등록
     *
     * @param connectionFactory : Redis와 실제로 연결할 수 있도록, 연결 정보를 가진 팩토리를 주입
     * @apiNote
     * @ template.setConnectionFactory(connectionFactory) : Redis와 실제로 통신할 수 있도록  연결 정보를 가진 팩토리를 주입
     * @ template.setKeySerializer(new StringRedisSerializer()) : Redis에 저장할 때 Key를 문자열(String)로
     * 직렬화
     * @ objectMapper : Java 8 날짜/시간 타입 지원 모듈(JavaTimeModule): 날짜를 timestamp(숫자) 대신 ISO-8601 문자열로
     * 저장하도록 설정
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // JSON 형태로 직렬화/역직렬화 할 수 있는 
        // GenericJackson2JsonRedisSerializer에 timestamp 커스텀한 objectMapper 적용
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
            objectMapper);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        return template;
    }

    /**
     * RedisMessageBroker가 구독할 채널(이사계획 새로운 초대자 알림 채널) 설정
     */
    @Bean
    public ChannelTopic notificationTopic() {
        return new ChannelTopic("moving-plan-notifications");
    }
} 