package com.example.p24zip.global.exception;

import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

@Getter
public class AdditionalInfoRequiredException extends OAuth2AuthenticationException {

    private final String code;
    // 프론트에 전달할 임시 토큰
    private final String tempToken;

    public AdditionalInfoRequiredException(String code, String message, String tempToken) {
        super(message);
        this.code = code;
        this.tempToken = tempToken;
    }

}