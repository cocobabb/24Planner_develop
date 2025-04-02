package com.example.p24zip.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthSignupResponseDto {

    private final String nickname;
    private final String accessToken;

    public static OAuthSignupResponseDto from(String nickname, String accessToken){
        return OAuthSignupResponseDto.builder()
            .nickname(nickname)
            .accessToken(accessToken)
            .build();
    }

}
