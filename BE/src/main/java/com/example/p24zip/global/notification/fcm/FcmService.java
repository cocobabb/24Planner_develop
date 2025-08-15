package com.example.p24zip.global.notification.fcm;

import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.nimbusds.oauth2.sdk.GeneralException;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private String FIREBASE_CONFIG_PATH = "firebase/firebase_service_key.json";
    @Value("${FCM_PROJECT_ID}")
    private String fcm_project_id;
    private String API_URL =
        "https://fcm.googleapis.com/v1/projects/" + fcm_project_id + "/messages:send";

    // FCM Web SDK의 vapidKey을 통해 브라우저 또는 앱이 FCM 서버와 직접 통신하여 토큰을 생성 후 레디스에 저장
    public void saveFcmDeviceToken(FcmCreateTokenRequestDto requestDTO, User user)
        throws IOException {

        stringRedisTemplate.opsForValue()
            .set(user.getUsername() + ":deviceToken:web", requestDTO.getDeviceToken());
    }

    // FCM deviceToken, 알림 내용을 전달받아 FCM 서버로 알림 전송
    public void sendMessageTo(String deviceToken, String title, String body) throws IOException {

        String message = makeMessage(deviceToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(
            message, // 만든 message body에 넣기
            MediaType.get("application/json; charset=utf-8")
        );

        // FCM 서버로 알림 넘겨줌 -> 토큰으로 구분하여 FCM 서버에서 알림 전송
        Request request = null;
        try {
            request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken()) // header에 포함
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();
        } catch (GeneralException e) {
            throw new RuntimeException(e);
        }
        Response response = client.newCall(request).execute(); // 요청 보냄

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
                    .text(body)
                    .image(null)
                    .build()
                ).build()
            ).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    // Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받는다.(Firebase와의 Authentication)
    private String getAccessToken() throws GeneralException {

        try {
            final GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();
            log.info("access token: {}", googleCredentials.getAccessToken());
            return googleCredentials.getAccessToken().getTokenValue();

        } catch (IOException e) {
            throw new GeneralException(String.valueOf(CustomCode.GOOGLE_REQUEST_TOKEN_ERROR));
        }
    }


}