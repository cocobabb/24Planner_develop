package com.example.p24zip.global.notification;

import com.example.p24zip.global.exception.CustomException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
public class SseEmitterPool {

    // userId → SseEmitter 맵 (멀티 스레드 환경 고려 ConcurrentHashMap 사용)
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

        System.out.println(username + "님 sse 연결됨");

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
        connect(username);
        try {
            SseEmitter emitter = emitters.get(username);
            emitter.send(SseEmitter.event().name("newHousemate").data(data));
            System.out.println(username + "님에게 알림 내용 전달 성공");
        } catch (IOException e) {
            emitters.remove(username);
            log.error("SseEmitterPool의 send ERROR: " + e.getMessage());
            log.error(username + "님에게 알림 내용 전달 실패");
            System.out.println(username + "님에게 알림 내용 전달 실패");
            throw new CustomException("FAIL_SENDING_TO_SSE", "SSE에 알림 내용 전달에 실패했습니다");
        }
        remove(username);
    }


    /**
     * 특정 유저의 SSE 연결 제거 (필요 시)
     */
    public void remove(String username) {
        emitters.remove(username);
    }
}
