package com.example.p24zip.domain.chat.dto.response;

import com.example.p24zip.domain.chat.entity.Chat;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLastReadResponseDto {

    private Long messageId;
    private Long movingPlanId;
    private String chatMessage;
    private String writer;
    private ZonedDateTime timestamp;
    private String reader;

    // RedisChatDto로부터 변환하는 생성자
    public UserLastReadResponseDto(Chat chat, String reader) {
        this.messageId = chat.getId();
        this.movingPlanId = chat.getMovingPlan().getId();
        this.chatMessage = chat.getText();
        this.writer = chat.getUser().getNickname();
        this.timestamp = ZonedDateTime.from(chat.getCreatedAt());
        this.reader = reader;
    }


}
