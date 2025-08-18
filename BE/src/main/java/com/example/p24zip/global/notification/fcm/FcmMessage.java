package com.example.p24zip.global.notification.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmMessage {

    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {

        private Notification notification;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {

        private String title;
        private String body;
        private String image; // 알림창에 보일 이미지 설정 관련
    }

}
