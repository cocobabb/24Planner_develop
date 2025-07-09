package com.example.p24zip.domain.movingPlan.service;

import com.example.p24zip.domain.movingPlan.dto.response.HousemateNotificationDto;
import com.example.p24zip.domain.movingPlan.dto.response.NotificationResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.RedisNotificationDto;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final String NOTIFICATION_CHANNEL = "moving-plan-notifications";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final ChannelTopic notificationTopic;
    private final ObjectMapper objectMapper;

    /**
     * 유저별로 SSE 연결을 생성하고, 연결 종료(정상/타임아웃) 시 자동으로 정리되도록 관리하는 메서드
     *
     * @return SseEmitter : 생성한 emitter를 반환하여, Controller에서 클라이언트에게 SSE 연결을 반환할 수 있게 합니다
     * @apiNote 1분(60, 000ms) 동안 아무 데이터도 전송되지 않으면 자동으로 연결이 끊어집니다(타임아웃).
     */
    public SseEmitter createEmitter(String username) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 타임아웃 :

        // username(유저명)을 key로, emitter를 emitters 맵에 저장합니다.
        //emitters는 현재 SSE로 연결된 모든 유저의 연결 정보를 관리하는 Map
        emitters.put(username, emitter);

        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(() -> emitters.remove(username));

        return emitter;
    }

    /**
     * 새로운 하우스메이트가 플랜에 참여하면, 기존 하우스메이트들에게 알림을 Redis에 저장하고, Pub/Sub으로 실시간 알림을 발행하는 메서드
     *
     * @param housemateNotificationDto : 새로 추가된 사용자 아이디, 이사계획 이름, 기존 Housemate 사용자 목록
     * @return void
     * @apiNote | RedisNotificationDto : Redis에 저장될 알림 내용 데이터를 가진 dto | saveNotificationToRedis:
     * Redis에 알림내용 저장하는 메서드 | redisTemplate.convertAndSend(topic,  Redis에 저장될 알림 내용 데이터를 가진 dto) :
     * topic을 구독하고 있는 sub들에게 알림 내용 전송
     */
    public void publishNotification(HousemateNotificationDto housemateNotificationDto) {
        // 기존 Housemate들에게 알림 발행
        housemateNotificationDto.getExistingHousemateUsernames().forEach(username -> {
            // Redis 알림 DTO 생성
            RedisNotificationDto redisNotificationDto = RedisNotificationDto.builder()
                .username(username)
                .type("newHousemate")
                .message(String.format("%s님이 [%s]에 참여했습니다.",
                    housemateNotificationDto.getNewHousemateUsername(),
                    housemateNotificationDto.getMovingPlanTitle()))
                .build();

            saveNotificationToRedis(redisNotificationDto);

            redisTemplate.convertAndSend(NOTIFICATION_CHANNEL, redisNotificationDto);
        });
    }

    /**
     * 알림을 Redis 해시에 저장
     *
     * @return void
     * @apiNote notifications:알림 받을 사용자 아이디+HashId(UUID로 만든 String) 를 key로  알림 받을 사용자 아이디, 어떤 메서드로
     * 저장된 데이터인지 나타낼 type, 알림내용, 알림이 발생한 시간, 알림 읽음 여부, redisKey
     */
    public void saveNotificationToRedis(RedisNotificationDto notification) {

        redisTemplate.opsForHash().put(
            "notifications:" + notification.getUsername(),
            notification.getId(),
            notification
        );

        redisTemplate.expire("notifications:" + notification.getUsername(), Duration.ofDays(7));
    }

    /**
     * 접속한 사용자의 모든 알림을 Redis에서 조회해, 클라이언트에 반환할 DTO 리스트로 변환하는 메서드
     *
     * @param user : 접속한 사용자
     * @return List<NotificationResponseDto> : Redis로 부터 가져온 알림 객체 리스트
     */
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

    /**
     * 접속한 사용자의 읽지 않은 알림만 골라서, 클라이언트에 반환할 DTO 리스트로 변환하는 메서드
     *
     * @param user : 접속한 사용자
     * @return List<NotificationResponseDto> : Redis로 부터 읽음 처리 안된 알림 객체 리스트
     */
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

    /**
     * Redis에서 알림 받을 사용자 아이디로 알림 데이터를 꺼내오는 메서드
     *
     * @param username : 알림을 받을 사용자 아이디
     * @return List<RedisNotificationDto> :  알림 받을 사용자 아이디, 어떤 메서드로 저장된 데이터인지 나타낼 type, 알림내용, 알림이
     * 발생한 시간, 알림 읽음 여부, redisKey을 가진 객체 리스트
     */
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

    /**
     * Redis로 부터 접속한 사용자의 특정 알림 데이터 가져와 읽음 처리 후 다시 저장
     *
     * @param user           : 접속한 사용자
     * @param notificationId : redisKey
     * @return void
     * @apiNote Redis로 부터 알림을 가지고 와, isRead 값을 true로 변경 후 다시 Redis에 저장
     */
    public void markNotificationAsRead(User user, String notificationId) {
        RedisNotificationDto notification = findNotificationInRedis(user.getUsername(),
            notificationId);

        if (notification != null) {
            notification.markAsRead();

            redisTemplate.opsForHash().put(
                "notifications:" + user.getUsername(),
                notificationId,
                notification
            );
        }
    }

    /**
     * Redis로 부터 접속한 사용자의 알림 리스트를 가져와 전부 읽음 처리 후 다시 저장
     *
     * @param user : 접속한 사용자
     * @return void
     * @apiNote Redis로 부터 알림을 가지고 와, isRead 값을 true로 변경 후 다시 Redis에 저장
     */
    public void markAllNotificationsAsRead(User user) {
        List<RedisNotificationDto> notifications = getUserNotificationsFromRedis(
            user.getUsername());

        List<RedisNotificationDto> updatedNotifications = notifications.stream()
            .map(notification -> {
                notification.markAsRead();
                return notification;
            })
            .collect(Collectors.toList());

        Map<String, RedisNotificationDto> notificationMap = updatedNotifications.stream()
            .collect(Collectors.toMap(
                RedisNotificationDto::getId,
                notification -> notification
            ));

        // 모든 알림을 한 번에 업데이트
        redisTemplate.opsForHash().putAll(
            "notifications:" + user.getUsername(),
            notificationMap
        );
    }

    /**
     * 특정 유저의 특정 알림을 Redis에서 찾아서, RedisNotificationDto 객체로 반환하는 메서드
     *
     * @param username       : 알림 받을 사용자 아이디
     * @param notificationId : Redis key 만들 때 구분 되기 위한 hash key가 될 redisKey
     * @return RedisNotificationDto : Redis에 저장된 알림 데이터 dto(알림 받을 사용자 아이디, 어떤 메서드로 저장된 데이터인지 나타낼
     * type, 알림내용, 알림이 * 발생한 시간, 알림 읽음 여부, redisKey을 가진 객체 )
     * @apiNote 매개변수를 이용해 만든 Redis key로 데이터 가져와, RedisNotificationDto 형태로 역직렬화
     */
    private RedisNotificationDto findNotificationInRedis(String username, String notificationId) {
        Object obj = redisTemplate.opsForHash()
            .get("notifications:" + username, notificationId);
        RedisNotificationDto redisNotification = objectMapper.convertValue(obj,
            RedisNotificationDto.class);

        return redisNotification;
    }

    /**
     * Redis Pub/Sub을 이용해 알림 메시지를 실시간으로 수신하고, 해당 알림을 SSE(Server-Sent Events)로 클라이언트(유저)에게 전송하는 리스너를
     * 등록하는 메서드
     *
     * @return void
     * @apiNote redisMessageListenerContainer.addMessageListener를 통해 지정한 채널(topic)
     * "moving-plan-notifications"에 대한 메시지 리스너를 등록하여 알림 메세지가 오면 역직렬화(객체로 변환)하여 설정한 Dto 타입으로 데이터 변환하여
     * SSE 실시간 알림 메서드로 전달
     */
    public void setupRedisMessageListener() {
        redisMessageListenerContainer.addMessageListener((message, pattern) -> {
            // redis에 저장된 직렬화 데이터를 역직렬화
            Object obj = redisTemplate.getValueSerializer().deserialize(message.getBody());
            // 역직렬화 된 데이터 객체를 Dto타입으로 변환
            RedisNotificationDto notification = objectMapper.convertValue(obj,
                RedisNotificationDto.class);

            if (notification != null) {
                // SSE로 실시간 알림 전송
                sendNotification(notification.getUsername(), notification);
            }
        }, notificationTopic);
    }

    /**
     * 특정 사용자(moving-plan-notifications을 구독하고 있는 사용자)에게 SSE(Server-Sent Events)로 실시간 알림을 전송하는 메서드
     *
     * @return void
     * @apiNote SSE 객체가 비어있지 않으면 "newHousemate"란 이벤트 이름으로 새로운 초대자 정보 전송
     */
    public void sendNotification(String username, Object notification) {
        SseEmitter emitter = emitters.get(username);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("newHousemate").data(notification));
            } catch (IOException e) {
                emitters.remove(username);
                log.error("sendNotification ERROR: " + e.getMessage());
                System.out.println("알림 내용 전달 실패");
                throw new CustomException("FAIL_SENDING_TO_SSE", "SSE에 알림 내용 전달에 실패했습니다");
            }
        }
    }
}