package com.example.p24zip.global;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class MyChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // WebSocket 연결 전에 처리할 로직
        if (message.getHeaders().containsKey("simpSessionId")) {
            System.out.println("WebSocket 연결됨. 세션 ID: " + message.getHeaders().get("simpSessionId"));
        }

        // 메시지 처리 전에 로깅
        System.out.println("메시지 전송: " + message);

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // 메시지가 성공적으로 전송된 후 처리할 로직
        System.out.println("메시지가 성공적으로 전송됨: " + message);
    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        // 메시지가 수신되기 전에 처리할 로직
        return true;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        // 메시지를 수신한 후 처리할 로직
        System.out.println("수신된 메시지: " + message);
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        // 메시지 전송 완료 후 처리할 로직
        if (ex != null) {
            System.out.println("메시지 전송 오류: " + ex.getMessage());
        }
    }
}