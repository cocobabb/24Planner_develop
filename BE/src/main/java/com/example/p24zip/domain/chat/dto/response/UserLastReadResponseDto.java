package com.example.p24zip.domain.chat.dto.response;

import com.example.p24zip.domain.chat.entity.Chat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLastReadResponseDto {

    private Long messageId;
    private Long movingPlanId;
    private String chatMessage;
    private Long writerId;
    private String writerNickname;
    private ZonedDateTime timestamp;
    private Long firstMessageId = 0L;

    // RedisChatDto로부터 변환하는 생성자
    @Builder
    public UserLastReadResponseDto(Chat chat) {
        this.messageId = chat.getId();
        this.movingPlanId = chat.getMovingPlan().getId();
        this.chatMessage = chat.getText();
        this.writerId = chat.getUser().getId();
        this.writerNickname = chat.getUser().getNickname();
        this.timestamp = ZonedDateTime.from(chat.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")));
    }

    public void changeMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void setFirstMessageId(Long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public void changeWriterNickname(String writerNickname) {
        this.writerNickname = writerNickname;
    }

}
