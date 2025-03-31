package com.example.p24zip.domain.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatsResponseDto {

    private final List<MessageResponseDto> Chats;

    public static ChatsResponseDto from(List<MessageResponseDto> chat) {
        return ChatsResponseDto.builder()
                .Chats(chat)
                .build();
    }
}
