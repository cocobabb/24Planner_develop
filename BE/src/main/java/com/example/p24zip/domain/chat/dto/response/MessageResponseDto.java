package com.example.p24zip.domain.chat.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResponseDto {

    private final Long messageId;
    private final String text;
    private final String nickname;
    private final String createTime;
    private final String createDay;

    public static MessageResponseDto from(Long messageId, String text, String nickname,
        String createTime, String createDay) {
        return MessageResponseDto.builder()
            .messageId(messageId)
            .text(text)
            .nickname(nickname)
            .createTime(createTime)
            .createDay(createDay)
            .build();
    }
}
