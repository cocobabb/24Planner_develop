package com.example.p24zip.domain.chat.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResponseDto {

    private final String text;
    private final String nickname;
    private final String createTime;

    public static MessageResponseDto from(String text, String nickname, String createTime) {
        return MessageResponseDto.builder()
                .text(text)
                .nickname(nickname)
                .createTime(createTime)
                .build();
    }
}
