package com.example.p24zip.domain.chat.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatsResponseDto {

    private final Long lastReadMessageId;
    private final List<MessageResponseDto> chats;

    public static ChatsResponseDto from(Long lastReadMessageId, List<MessageResponseDto> chat) {
        return ChatsResponseDto.builder()
            .lastReadMessageId(lastReadMessageId)
            .chats(chat)
            .build();
    }
}
