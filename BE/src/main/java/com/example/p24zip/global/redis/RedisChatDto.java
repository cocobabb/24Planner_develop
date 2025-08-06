package com.example.p24zip.global.redis;

import com.example.p24zip.domain.user.entity.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private User writer; // 작성자 아이디
    private ZonedDateTime timestamp;


    @Builder
    public RedisChatDto(Long messageId, Long movingPlanId, String chatMessage, User writer,
        LocalDateTime timestamp) {
        this.messageId = messageId;
        this.movingPlanId = movingPlanId;
        this.chatMessage = chatMessage;
        this.writer = writer;
        this.timestamp = timestamp.atZone(ZoneId.of("Asia/Seoul"));
    }

}
