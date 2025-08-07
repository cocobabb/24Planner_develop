package com.example.p24zip.domain.movingPlan.controller;

import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.notification.NotificationResponseDto;
import com.example.p24zip.global.notification.NotificationService;
import com.example.p24zip.global.notification.SseEmitterPool;
import com.example.p24zip.global.redis.RedisNotificationDto;
import com.example.p24zip.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SseEmitterPool sseEmitterPool;
    private final NotificationService notificationService;

    // SSE 구독 엔드포인트 (기존 코드 유지)
    @GetMapping(value = "/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal User user) {

        return sseEmitterPool.connect(user.getUsername());
    }

    // 모든 알림 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUserNotifications(
        @AuthenticationPrincipal User user
    ) {
        List<NotificationResponseDto> notifications = notificationService.getUserNotifications(
            user);

        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    // 읽지 않은 알림 조회
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUserUnreadNotifications(
        @AuthenticationPrincipal User user
    ) {
        List<NotificationResponseDto> unreadNotifications = notificationService.getUserUnreadNotifications(
            user);

        return ResponseEntity.ok(ApiResponse.ok(unreadNotifications));
    }

    // 읽지 않은 알림 조회하여 다시 알림 처리
    @GetMapping("/sse/resend")
    public void checkUnreadNotification(@AuthenticationPrincipal User user) {
        // 여기서만 unread 알림을 전송
        List<NotificationResponseDto> unreadNotifications = notificationService.getUserUnreadNotifications(
            user).reversed();
        for (NotificationResponseDto notification : unreadNotifications) {
            sseEmitterPool.send(user.getUsername(), notification);
        }

    }

    // 특정 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(
        @AuthenticationPrincipal User user,
        @PathVariable String notificationId
    ) {
        notificationService.markNotificationAsRead(user, notificationId);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 모든 알림 읽음 처리
    @PatchMapping("/read")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsAsRead(
        @AuthenticationPrincipal User user
    ) {
        notificationService.markAllNotificationsAsRead(user);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // Redis에서 직접 알림 조회 (디버깅용)
    @GetMapping("/redis")
    public ResponseEntity<ApiResponse<List<RedisNotificationDto>>> getUserNotificationsFromRedis(
        @AuthenticationPrincipal User user
    ) {
        List<RedisNotificationDto> notifications =
            notificationService.getUserNotificationsFromRedis(user.getUsername());

        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }
}