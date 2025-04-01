package com.example.p24zip.domain.user.dto.response;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FindPasswordResponseDto {
    private final String tempToken;
    private final String expiredAt;
}
