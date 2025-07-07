package com.example.p24zip.domain.movingPlan.service;

import com.example.p24zip.domain.movingPlan.dto.response.HousemateNotificationDto;
import com.example.p24zip.domain.movingPlan.dto.response.NotificationResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.RedisNotificationDto;
import com.example.p24zip.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final String NOTIFICATION_CHANNEL = "moving-plan-notifications";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final ChannelTopic notificationTopic;
    private final ObjectMapper objectMapper;

    // SSE 연결 생성 (기존 코드 유지)
    public SseEmitter createEmitter(String username) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 타임아웃
        emitters.put(username, emitter);

        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(() -> emitters.remove(username));

        return emitter;
    }

    // Redis Pub/Sub을 통한 알림 발행
    public void publishNotification(HousemateNotificationDto housemateNotificationDto) {
        // 기존 Housemate들에게 알림 발행
        housemateNotificationDto.getExistingHousemateUsernames().forEach(username -> {
            // Redis 알림 DTO 생성
            RedisNotificationDto redisNotificationDto = RedisNotificationDto.builder()
                .username(username)
                .type("newHousemate")
                .message(String.format("%s님이 \"%s\" 플랜에 참여했습니다.",
                    housemateNotificationDto.getNewHousemateName(),
                    housemateNotificationDto.getMovingPlanTitle()))
                .build();

            // Redis에 알림 저장
            saveNotificationToRedis(redisNotificationDto);

            // Redis Pub/Sub으로 알림 발행
            redisTemplate.convertAndSend(NOTIFICATION_CHANNEL, redisNotificationDto);
        });
    }

    // Redis에 알림 저장
    public void saveNotificationToRedis(RedisNotificationDto notification) {
        // 알림을 Redis 해시에 저장
        redisTemplate.opsForHash().put(
            "notifications:" + notification.getUsername(),
            notification.getId(),
            notification
        );
    }

    // Redis에서 사용자의 알림 조회
    public List<NotificationResponseDto> getUserNotifications(User user) {
        List<RedisNotificationDto> redisNotifications = getUserNotificationsFromRedis(
            user.getUsername());

        return redisNotifications.stream()
            .map(notification -> NotificationResponseDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .timestamp(notification.getTimestamp())
                .read(notification.isRead())
                .build())
            .collect(Collectors.toList());
    }

    // Redis에서 사용자의 읽지 않은 알림 조회
    public List<NotificationResponseDto> getUserUnreadNotifications(User user) {
        List<RedisNotificationDto> redisNotifications = getUserNotificationsFromRedis(
            user.getUsername());

        return redisNotifications.stream()
            .filter(notification -> !notification.isRead())
            .map(notification -> NotificationResponseDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .timestamp(notification.getTimestamp())
                .read(notification.isRead())
                .build())
            .collect(Collectors.toList());
    }

    // Redis에서 사용자의 알림 조회 (내부 메서드)
    public List<RedisNotificationDto> getUserNotificationsFromRedis(String username) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return redisTemplate.opsForHash()
            .values("notifications:" + username)
            .stream()
            .map(obj -> {
                if (obj instanceof RedisNotificationDto) {
                    return (RedisNotificationDto) obj;
                } else {
                    // LinkedHashMap 등으로 역직렬화된 경우 ObjectMapper로 변환
                    return objectMapper.convertValue(obj, RedisNotificationDto.class);
                }
            })
            .collect(Collectors.toList());
    }

    // 알림 읽음 처리 (Redis)
    public void markNotificationAsRead(User user, String notificationId) {
        RedisNotificationDto notification = findNotificationInRedis(user.getUsername(),
            notificationId);

        if (notification != null) {
            // 깊은 복사 후 읽음 처리
            RedisNotificationDto updatedNotification = new RedisNotificationDto(notification);
            updatedNotification.markAsRead();

            // Redis에 업데이트된 알림 저장
            redisTemplate.opsForHash().put(
                "notifications:" + user.getUsername(),
                notificationId,
                updatedNotification
            );
        }
    }

    // 모든 알림 읽음 처리 (Redis)
    public void markAllNotificationsAsRead(User user) {
        List<RedisNotificationDto> notifications = getUserNotificationsFromRedis(
            user.getUsername());

        List<RedisNotificationDto> updatedNotifications = notifications.stream()
            .map(notification -> {
                RedisNotificationDto updatedNotification = new RedisNotificationDto(notification);
                updatedNotification.markAsRead();
                return updatedNotification;
            })
            .collect(Collectors.toList());

        // 모든 알림을 한 번에 업데이트
        Map<String, RedisNotificationDto> notificationMap = updatedNotifications.stream()
            .collect(Collectors.toMap(
                RedisNotificationDto::getId,
                notification -> notification
            ));

        redisTemplate.opsForHash().putAll(
            "notifications:" + user.getUsername(),
            notificationMap
        );
    }

    // Redis에서 특정 알림 찾기
    private RedisNotificationDto findNotificationInRedis(String username, String notificationId) {
        Object obj = redisTemplate.opsForHash()
            .get("notifications:" + username, notificationId);
        RedisNotificationDto redisNotification = objectMapper.convertValue(obj,
            RedisNotificationDto.class);

        return redisNotification;
    }

    // Redis 메시지 리스너 설정
    public void setupRedisMessageListener() {
        redisMessageListenerContainer.addMessageListener((message, pattern) -> {
            // 역직렬화된 메시지 처리
            Object obj = redisTemplate.getValueSerializer().deserialize(message.getBody());
            RedisNotificationDto notification = objectMapper.convertValue(obj,
                RedisNotificationDto.class);

            if (notification != null) {
                // SSE로 실시간 알림 전송
                sendNotification(notification.getUsername(), notification);
            }
        }, notificationTopic);
    }

    // SSE로 알림 전송
    public void sendNotification(String username, Object notification) {
        SseEmitter emitter = emitters.get(username);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("newHousemate").data(notification));
            } catch (IOException e) {
                emitters.remove(username);
            }
        }
    }
}