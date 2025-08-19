package com.example.p24zip.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final String errorCode;

    public CustomException(CustomCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }
}
