package com.example.p24zip.global.redis;

import com.example.p24zip.domain.movingPlan.repository.HousemateRepository;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.global.notification.SseEmitterPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseEmitterPool emitterPool;
    private final HousemateRepository housemateRepository;
    private final MovingPlanRepository movingPlanRepository;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel()); // ex) movingPlan:123:uuid
        String body = new String(message.getBody());

        try {
            String movingPlanId = channel.split(":")[1];
            String uuid = channel.split(":")[2];

            // redis 메시지 -> 객체 변환 (redis는 String key:String data 형태로 저장되어 있기 때문)
            RedisNotificationDto dto = objectMapper.readValue(body, RedisNotificationDto.class);

            // 이사계획의 기존 사용자에게 SSE 전송
            emitterPool.send(dto.getUsername(), dto);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("RedisSubscriber의 onMessage메서드 ERROR: ", e.getMessage());
        }
    }


}
