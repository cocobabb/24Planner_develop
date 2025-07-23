package com.example.p24zip.global.notification;

import com.example.p24zip.global.redis.RedisNotificationDto;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationResponseDto {

    private String id;
    private String type;
    private String message;
    private ZonedDateTime timestamp;
    private boolean read;

    @Builder
    public NotificationResponseDto(String id, String type, String message, ZonedDateTime timestamp,
        boolean read) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    // RedisNotificationDto로부터 변환하는 생성자
    public NotificationResponseDto(RedisNotificationDto redisNotification) {
        this.id = redisNotification.getId();
        this.type = redisNotification.getType();
        this.message = redisNotification.getMessage();
        this.timestamp = redisNotification.getTimestamp();
        this.read = redisNotification.isRead();
    }
} 