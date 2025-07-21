package com.example.p24zip.global.redis;

import com.example.p24zip.domain.movingPlan.dto.response.RedisNotificationDto;
import com.example.p24zip.domain.movingPlan.entity.Housemate;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.HousemateRepository;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.notification.SseEmitterPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
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
        String channel = new String(message.getChannel()); // ex) movingPlan:123
        String body = new String(message.getBody());

        try {
            String movingPlanId = channel.split(":")[1];

            // 이사계획의 멤버 ID 리스트 조회
            MovingPlan movingPlan = movingPlanRepository.findById(Long.valueOf(movingPlanId))
                .orElseThrow(
                    () -> new CustomException("INVALID_INVITATION", "만료되었거나 유효하지 않은 초대 링크입니다."));
            List<Housemate> existingHousemates = housemateRepository.findByMovingPlan(movingPlan);
            List<String> existingHousemateUsernames = existingHousemates.stream()
                .map(h -> h.getUser().getUsername())
                .collect(Collectors.toList());

            // redis 메시지 -> 객체 변환 (redis는 String key:String data 형태로 저장되어 있기 때문)
            RedisNotificationDto dto = objectMapper.readValue(body, RedisNotificationDto.class);

            // 각 사용자에게 SSE 전송
            for (String username : existingHousemateUsernames) {
                emitterPool.send(username, dto);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("RedisSubscriber의 onMessage: ", e.getMessage());
        }
    }


}
