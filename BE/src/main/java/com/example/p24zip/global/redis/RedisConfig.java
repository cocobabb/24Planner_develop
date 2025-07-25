package com.example.p24zip.global.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisSubscriber redisSubscriber;
    private final RedisConnectionFactory connectionFactory;

    /**
     * Redis MessageBroker 설정
     *
     * @return RedisMessageListenerContainer
     * @apiNote <p> connectionFactory : Redis와 실제로 연결할 수 있도록, 연결 정보를 가진 팩토리를 주입</p>
     * <p>RedisMessageListenerContainer : Redis Pub/Sub 메시지를 수신하는 이벤트 루프 역할</p>
     * <p>RedisMessageListenerContainer.setConnectionFactory(connectionFactory) : Redis와 실제로 통신할 수
     * 있도록 연결 정보를 가진 connectionFactory를 컨테이너에 주입</p>
     * <p> RedisMessageListenerContainter.addMessageListener : 메시지를 수신할 대상과 구독할 채널(또는 패턴)을 지정</p>
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // movingPlan:으로 시작하는 채널 모두 sub
        container.addMessageListener(redisSubscriber, new PatternTopic("movingPlan:*"));

        return container;
    }

    /**
     * Key는 문자열, Value는 JSON(커스텀 ObjectMapper)으로 직렬화하여 Redis에 저장/조회할 수 있는 RedisTemplate을 Bean으로 등록
     *
     * @apiNote <p> connectionFactory : Redis와 실제로 연결할 수 있도록, 연결 정보를 가진 팩토리를 주입</p>
     * <p>template.setConnectionFactory(connectionFactory) : Redis와 실제로 통신할 수 있도록  연결 정보를 가진 팩토리를
     * 주입</p>
     * <p>
     * template.setKeySerializer(new StringRedisSerializer()) : Redis에 저장할 때 Key를 문자열(String)로
     * 직렬화</p>
     * <p>objectMapper : Java 8 날짜/시간 타입 지원 모듈(JavaTimeModule): 날짜를 timestamp(숫자) 대신 ISO-8601 문자열로
     * 저장하도록 설정</p>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
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


} 