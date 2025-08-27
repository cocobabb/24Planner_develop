package com.example.p24zip.global.notification.fcm;

import com.example.p24zip.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
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

    // 알림 내용 전송할 FCM 서버 주소
    private String getApiUrl() {
        return "https://fcm.googleapis.com/v1/projects/" + fcmProjectId + "/messages:send";
    }


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
     * @param deviceToken : 어떤 클라이언트에서 온 건지 구분하는 토큰
     * @param title:      알림에 표시될 제목
     * @param body:       알림에 표시될 내용
     * @return void
     **/
    public void sendMessageTo(String deviceToken, String title, String body)
        throws IOException {

        // 자격 증명이 만료되면 자동 갱신
        googleCredentials.refreshIfExpired();

        // 인증•인가된 googleCredentials 객체로 토큰 가져와 firebase 서버에 데이터 전송
        String accessToken = googleCredentials.getAccessToken().getTokenValue();
        // 직렬화된 message 객체 생성
        String message = makeMessage(deviceToken, title, body);
        log.info("sendMessageTo의 메세지: {}", message);

        RequestBody requestBody = RequestBody.create(
            message, // 직렬화 된 message를 body에 넣기
            MediaType.get("application/json; charset=utf-8")
        );

        // FCM 서버로 알림 넘겨줌 -> 토큰으로 구분하여 FCM 서버에서 알림 전송
        Request request = null;
        log.info("FCM-sendMessageTo의 accessToken: {}", accessToken);
        request = new Request.Builder()
            .url(getApiUrl())
            .post(requestBody)
            .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // header에 포함
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
            .build();

        // okHttpClient의 비동기 방식
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("FCM 전송 실패", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.info("FCM 전송 성공: {}", response.body().string());
            }
        });

    }


    /**
     * FCM 전송 정보를 기반으로 메시지를 구성 (Object -> String)
     *
     * @param targetToken : 어떤 클라이언트에서 온 건지 구분하는 토큰
     * @param title:      알림에 표시될 제목
     * @param body:       알림에 표시될 내용
     * @return String : FCM 전송 정보를 가진 String
     **/
    private String makeMessage(String targetToken, String title, String body)
        throws com.fasterxml.jackson.core.JsonProcessingException { // JsonParseException, JsonProcessingException
        FcmMessage fcmMessage = FcmMessage.builder()
            .message(FcmMessage.Message.builder()
                .token(targetToken)
                .notification(FcmMessage.Notification.builder()
                    .title(title) // FCM에 보내질 객체 필드 이름 정해져 있음 (반드시, title과 body를 포함해야함)
                    .body(body)
                    .image(null)
                    .build()
                ).build()
            ).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }


}