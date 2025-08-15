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
        private String text;
        private String image; // 다른 알림에서 쓰일 때를 대비하여 확장성 있게 이미지도 설정
    }

}
