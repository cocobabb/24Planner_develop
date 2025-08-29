package com.example.p24zip.global.notification.fcm;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TestFcmMessage {

    private Set<String> tokens;
    private Notification notification;


    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {

        private String title;
        private String body;
    }

}
