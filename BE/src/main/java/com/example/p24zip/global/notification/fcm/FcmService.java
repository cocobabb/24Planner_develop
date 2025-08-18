package com.example.p24zip.global.notification.fcm;

import com.example.p24zip.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.nimbusds.oauth2.sdk.GeneralException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private static final String FCM_TOKEN_REDIS_SET_KEY = "%s:deviceTokens";

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final GoogleCredentials googleCredentials;


    @Value("${FCM_PROJECT_ID}")
    private String fcmProjectId;

    private String getApiUrl() {
        return "https://fcm.googleapis.com/v1/projects/" + fcmProjectId + "/messages:send";
    }


    // FCM Web SDK의 vapidKey을 통해 브라우저 또는 앱이 FCM 서버와 직접 통신하여 토큰을 생성 후 레디스에 저장
    public void saveFcmDeviceToken(FcmCreateTokenRequestDto requestDTO, User user)
        throws IOException {

        System.out.println("deviceToken: " + requestDTO.getDeviceToken());
        System.out.println("username: " + user.getUsername());
        String key = String.format(FCM_TOKEN_REDIS_SET_KEY, user.getUsername());

        stringRedisTemplate.opsForSet()
            .add(key, requestDTO.getDeviceToken());

        stringRedisTemplate.expire(key, 3, TimeUnit.DAYS);
    }

    // FCM deviceToken, 알림 내용을 전달받아 FCM 서버로 알림 전송
    public void sendMessageTo(String deviceToken, String title, String body)
        throws IOException, GeneralException {

        // 자격 증명이 만료되면 자동 갱신
        googleCredentials.refreshIfExpired();

        // 인증•인가된 googleCredentials 객체로 토큰 가져와 firebase 서버에 데이터 전송
        String accessToken = googleCredentials.getAccessToken().getTokenValue();
        String message = makeMessage(deviceToken, title, body);
        System.out.println("sendMessageTo의 메세지: " + message);

        RequestBody requestBody = RequestBody.create(
            message, // 만든 message body에 넣기
            MediaType.get("application/json; charset=utf-8")
        );

        System.out.println("FCM-sendMessageTo의 requestBody: " + requestBody);

        // FCM 서버로 알림 넘겨줌 -> 토큰으로 구분하여 FCM 서버에서 알림 전송
        Request request = null;
        System.out.println("FCM-sendMessageTo의 accessToken: " + accessToken);
        request = new Request.Builder()
            .url(getApiUrl())
            .post(requestBody)
            .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // header에 포함
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
            .build();
        Response response = okHttpClient.newCall(request).execute(); // 요청 보냄

        System.out.println(response.body().string());
    }


    // FCM 전송 정보를 기반으로 메시지를 구성한다. (Object -> String)
    private String makeMessage(String targetToken, String title, String body)
        throws com.fasterxml.jackson.core.JsonProcessingException { // JsonParseException, JsonProcessingException
        FcmMessage fcmMessage = FcmMessage.builder()
            .message(FcmMessage.Message.builder()
                .token(targetToken)
                .notification(FcmMessage.Notification.builder()
                    .title(title)
                    .body(body)
                    .image(null)
                    .build()
                ).build()
            ).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }


}