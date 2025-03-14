package com.example.p24zip.global.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException() {
        super("요청한 리소스를 찾을 수 없습니다.");
    }
}