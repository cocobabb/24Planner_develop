package com.example.p24zip.domain.chat.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatsErrorResponseDto {

    private final String code;
    private final String message;
    private final String text;
}
