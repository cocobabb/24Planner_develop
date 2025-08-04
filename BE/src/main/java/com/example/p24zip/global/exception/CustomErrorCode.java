package com.example.p24zip.global.exception;

import lombok.Getter;

@Getter
public enum CustomErrorCode {
    EXIST_EMAIL("EXIST_EMAIL", "이미 사용중인 이메일입니다."),
    EXIST_NICKNAME("EXIST_NICKNAME", "이미 사용중인 닉네임입니다."),
    BAD_REQUEST("BAD_REQUEST", "필수값이 누락되거나 형식이 올바르지 않습니다."),
    TIME_OUT("TIME_OUT", "시간이 초과되었습니다."),
    TOOMANY_REQUEST("TOOMANY_REQUEST", "5초안에 다시 요청했습니다."),
    EMAIL_SEND_FAIL("EMAIL_SEND_FAIL", "메일 전송 중 오류가 발생했습니다."),
    SOCIAL_LOGIN("SOCIAL_LOGIN", "소셜 로그인은 비밀번호 찾기를 진행할 수 없습니다."),
    INVALID_INVITATION("INVALID_INVITATION", "만료되었거나 유효하지 않은 초대 링크입니다."),
    ALREADY_REGISTERED("ALREADY_REGISTERED", "이미 이 플랜의 동거인으로 등록되어 있습니다."),
    INVALID_DATE("INVALID_DATE", "시작 날짜는 종료 날짜보다 이전이어야 합니다."),
    NOT_EXIST_EMAIL("NOT_EXIST_EMAIL", "존재하지 않는 이메일입니다."),
    SOCIAL_LOGIN_NEEDED("SOCIAL_LOGIN_NEEDED", "소셜 로그인 회원입니다. 비밀번호 찾기를 진행할 수 없습니다."),
    NOT_SEARCH_CHAT_MESSAGE("NOT_SEARCH_CHAT_MESSAGE", "메시지를 찾을 수 없습니다.");

    private final String code;
    private final String message;

    CustomErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
} 