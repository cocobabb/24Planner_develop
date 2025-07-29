package com.example.p24zip.global.exception;

import lombok.Getter;
import org.springframework.mail.MailException;

@Getter
public class ConnectMailException extends MailException {

    private final String errorCode;

    public ConnectMailException(CustomErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }
}
