package com.example.p24zip.global.exception;


import lombok.extern.slf4j.Slf4j;

@Slf4j

public class StompTokenException extends RuntimeException {

    public StompTokenException(String message) {
        super(message);
    }
}
