package com.example.p24zip.global.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
public class SseEmitterPool {

    // username → SseEmitter 맵 (멀티 스레드 환경 고려 ConcurrentHashMap 사용)
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();


    /**
     * 새로운 SSE 연결 생성 및 등록
     *
     * @param username 로그인한 유저 ID
     * @return SseEmitter
     */
    public SseEmitter connect(String username) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);  // 타임아웃 무제한 설정
        emitters.put(username, emitter);

        System.out.println(username + "님 sse 연결");

        // 연결 종료 시 맵에서 제거
        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(() -> emitters.remove(username));
        emitter.onError((e) -> emitters.remove(username));

        return emitter;
    }

    /**
     * 특정 유저에게 SSE 이벤트 전송
     *
     * @param username 대상 유저 ID
     * @param data     전송할 데이터
     */
    public void send(String username, Object data) {
        int retry = 3;
        int count = 0;

        while (count < retry) {
            try {
                SseEmitter emitter = emitters.get(username);
                emitter.send(SseEmitter.event().name("newHousemate").data(data));
                System.out.println(username + "님에게 알림 내용 전달 성공");
                return; // 전송 성공했으니 종료
            } catch (Exception e) {
                count++;
                log.warn("[SSE 전송 실패 - 재시도 {}/{}] 사용자: {}, 에러: {}", count, retry, username,
                    e.getMessage());

                // 마지막 시도에서도 실패한 경우
                if (count == retry) {
                    emitters.remove(username);
                    log.error("[SSE 전송 실패] {}님에게 알림 전달 실패. 연결 제거됨", username);
                }

                // 짧은 대기 후 재시도
                try {
                    Thread.sleep(1000); // 1초 대기 후 재시도
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    /**
     * 특정 유저의 SSE 연결 제거 (필요 시)
     */
    public void remove(String username) {
        emitters.remove(username);
        System.out.println(username + "님에게 sse 연결 해제");
    }
}
