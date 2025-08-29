package com.example.p24zip.global.notification.fcm;

import com.example.p24zip.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private static final String FCM_TOKEN_REDIS_SET_KEY = "%s:deviceTokens";

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;


    /**
     * FCM Web SDK의 vapidKey을 통해 브라우저 또는 앱이 FCM 서버와 직접 통신하여 토큰을 생성 후 레디스에 저장
     *
     * @param requestDTO {deviceToken} : 클라이언트와 firebase 서버가 통신하여 생성한 각 클라이언트를 구분할 토큰
     * @param user       : 로그인한 사용자
     */
    public void saveFcmDeviceToken(FcmCreateTokenRequestDto requestDTO, User user) {

        log.info("FCM-saveFcmDeviceToken-{}의 deviceToken: {}", user.getUsername(),
            requestDTO.getDeviceToken());
        String key = String.format(FCM_TOKEN_REDIS_SET_KEY, user.getUsername());

        stringRedisTemplate.opsForSet()
            .add(key, requestDTO.getDeviceToken());

        // 토큰이 갱신될 경우를 생각하여 기존의 토큰이 쌓이지 않도록 만료일을 지정
        stringRedisTemplate.expire(key, 3, TimeUnit.DAYS);
    }


    /**
     * FCM deviceToken, 알림 내용을 전달받아 FCM 서버로 알림 전송
     *
     * @param deviceTokens : 어떤 클라이언트에서 온 건지 구분하는 토큰
     * @param title:       알림에 표시될 제목
     * @param body:        알림에 표시될 내용
     * @return void
     **/
    public void sendMessageTo(Set<String> deviceTokens, String title, String body) {

        // fcm 429 에러로 fcm 전송 방식 수정 => 한개의 요청(메세지)에 여러 개의 토큰을 담아서 보냄
        if (deviceTokens == null || deviceTokens.isEmpty()) {
            log.warn("전송할 디바이스 토큰이 없음");
            return;
        }

        MulticastMessage messages = MulticastMessage.builder()
            .addAllTokens(deviceTokens) // 최대 500개
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .build();

        // 해당 방식 Deprecated  됨
//        try {
//            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
//            String responseJson = objectMapper.writeValueAsString(response.getResponses());
//            log.info("FCM 전송 성공: {}", responseJson);
//
//        } catch (Exception e) {
//            log.error("FCM 전송 실패: {}", e.getMessage());
//        }

        // fcm 기기 토큰 batch 방식(동기)
//        try {
//            BatchResponse responses = FirebaseMessaging.getInstance()
//                .sendEachForMulticast(messages);
//            log.info("FCM 전송 성공");
//        } catch (Exception e) {
//            log.error("FCM 전송 실패: {}", e.getMessage(), e);
//        }

        // fcm 기기 토큰 batch 방식(비동기)
        ApiFuture<BatchResponse> future = FirebaseMessaging.getInstance()
            .sendEachForMulticastAsync(messages);

        CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(response -> {
            log.info("FCM 전송 성공");
        }).exceptionally(e -> {
            log.error("FCM 전송 실패: {}", e.getMessage(), e);
            return null;
        });

    }


}