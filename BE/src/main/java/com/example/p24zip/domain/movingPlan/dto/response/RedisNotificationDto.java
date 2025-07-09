package com.example.p24zip.domain.movingPlan.dto.response;

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class RedisNotificationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String type;
    private String message;
    private ZonedDateTime timestamp;
    private boolean read;
    private String redisKey;

    @Builder
    public RedisNotificationDto(String username, String type, String message, String redisKey) {
        this.id = java.util.UUID.randomUUID().toString();
        this.username = username;
        this.type = type;
        this.message = message;
        this.timestamp = ZonedDateTime.now();
        this.read = false;
        this.redisKey = redisKey;
    }

    // 알림 읽음 처리 메서드
    public void markAsRead() {
        this.read = true;
    }
} 