package com.example.p24zip.domain.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatsResponseDto {

    private final List<MessageResponseDto> chats;

    public static ChatsResponseDto from(List<MessageResponseDto> chat) {
        return ChatsResponseDto.builder()
                .chats(chat)
                .build();
    }
}
