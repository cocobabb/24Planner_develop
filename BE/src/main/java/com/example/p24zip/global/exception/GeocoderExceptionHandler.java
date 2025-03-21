package com.example.p24zip.global.exception;

import lombok.Getter;

@Getter
public class GeocoderExceptionHandler extends RuntimeException{
    private final String code;

    public GeocoderExceptionHandler(String code, String message) {
        super(message);
        this.code = code;
    }
}
