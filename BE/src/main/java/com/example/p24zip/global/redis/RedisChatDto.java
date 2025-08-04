package com.example.p24zip.global.redis;

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class RedisChatDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long messageId; // Chat의 id
    private Long movingPlanId;
    private String chatMessage;
    private String writer; // 작성자 닉네임
    private ZonedDateTime timestamp;


    @Builder
    public RedisChatDto(Long messageId, Long movingPlanId, String chatMessage, String writer) {
        this.messageId = messageId;
        this.movingPlanId = movingPlanId;
        this.chatMessage = chatMessage;
        this.writer = writer;
        this.timestamp = ZonedDateTime.now();
    }

}
