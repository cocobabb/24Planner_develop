package com.example.p24zip.global.exception;

import lombok.Getter;

@Getter
public enum CustomCode {

    // ASYNC
    EMAIL_SEND_FAIL("EMAIL_SEND_FAIL", "메일 전송 중 오류가 발생했습니다."),

    // FCM
    FCM_TOKEN_CREATE("FCM_TOKEN_CREATE", "FCM 토큰을 생성하였습니다."),
    FCM_SERVER_ERROR("FCM_SERVER_ERROR", "Firebase Key 불러오기 실패 "),
    GOOGLE_REQUEST_TOKEN_ERROR("GOOGLE_REQUEST_TOKEN_ERROR", "FCM 토큰이 유효하지 않습니다."),

    // CHAT

    FIRST_CHAT_MESSAGE("FIRST_CHAT_MESSAGE", "이전 메세지가 없습니다."),
    CHAT_MESSAGE_LOAD_SUCCESS("OK", "채팅 메시지 조회에 성공했습니다."),
    CHAT_MESSAGE_DELETE_SUCCESS("DELETED", "모든 채팅 메시지를 삭제했습니다."),


    // HOUSE
    HOUSE_CREATE_SUCCESS("CREATED", "집 생성에 성공했습니다."),
    HOUSE_LOAD_SUCCESS("OK", "집 조회에 성공했습니다."),
    HOUSE_COLLECTIONS_LOAD_SUCCESS("OK", "집 목록 조회에 성공했습니다."),
    HOUSE_NICKNAME_UPDATE_SUCCESS("UPDATED", "집 별칭 수정에 성공했습니다."),
    HOUSE_DETAIL_CONTENT_UPDATE_SUCCESS("UPDATED", "집 상세 내용 수정에 성공했습니다."),
    HOUSE_DETAIL_ADDRESS_UPDATE_SUCCESS("UPDATED", "집 상세주소 수정에 성공했습니다."),
    HOUSE_DELETE_SUCCESS("DELETED", "집 삭제에 성공했습니다."),
    GEOCODER_API_CONVERT_ERROR("GEOCODER_API_CONVERT_ERROR", "좌표 변경 API에서 변환 오류가 발생했습니다."),


    // HOUSEMATE
    HOUSEMATE_CREATE_SUCCESS("CREATED", "동거인 초대 링크 생성에 성공했습니다."),
    HOUSEMATE_DELETE_SUCCESS("DELETED", "동거인 삭제에 성공했습니다."),
    INVALID_INVITATION("INVALID_INVITATION", "만료되었거나 유효하지 않은 초대 링크입니다."),
    ALREADY_REGISTERED("ALREADY_REGISTERED", "이미 이 플랜의 동거인으로 등록되어 있습니다."),


    // INVITATION
    INVITATION_VALIDATE_SUCCESS("OK", "유효한 초대 링크입니다."),
    INVITATION_ACCEPTED_SUCCESS("ACCEPTED", "초대를 수락하고 동거인으로 등록되었습니다."),


    // MOVING PLAN
    MOVING_PLAN_CREATE_SUCCESS("CREATED", "플랜이 생성되었습니다."),
    MOVING_PLAN_COLLECTIONS_LOAD_SUCCESS("OK", "플랜 목록 조회에 성공했습니다."),
    MOVING_PLAN_LOAD_SUCCESS("OK", "플랜 조회에 성공했습니다."),
    MOVING_PLAN_TITLE_LOAD_SUCCESS("OK", "플랜 제목 조회에 성공했습니다."),
    MOVING_PLAN_TITLE_UPDATE_SUCCESS("UPDATED", "플랜 제목 수정에 성공했습니다."),
    MOVING_PLAN_DELETE_SUCCESS("DELETED", "플랜 삭제에 성공했습니다."),


    // SCHEDULE
    SCHEDULE_CREATE_SUCCESS("CREATED", "할 일 생성에 성공했습니다."),
    SCHEDULE_MONTH_LOAD_SUCCESS("OK", "할 일 월별 목록 조회에 성공했습니다."),
    SCHEDULE_DAY_LOAD_SUCCESS("OK", "할 일 날짜별 목록 조회에 성공했습니다."),
    SCHEDULE_UPDATE_SUCCESS("UPDATED", "할 일 수정에 성공했습니다."),
    SCHEDULE_DELETE_SUCCESS("DELETED", "할 일 삭제에 성공했습니다."),
    INVALID_DATE("INVALID_DATE", "시작 날짜는 종료 날짜보다 이전이어야 합니다."),


    // TASK
    TASK_CREATE_SUCCESS("CREATED", "체크포인트 생성에 성공했습니다."),
    TASK_COLLECTION_LOAD_SUCCESS("OK", "체크포인트 목록 조회에 성공했습니다."),
    TASK_CONTENT_UPDATE_SUCCESS("UPDATED", "체크포인트 내용 수정에 성공했습니다."),
    TASK_COMPLETE_UPDATE_SUCCESS("UPDATED", "체크포인트 완료 여부 수정에 성공했습니다."),
    TASK_DELETE_SUCCESS("DELETED", "체크포인트 삭제에 성공했습니다."),


    // TASK GROUP
    TASK_GROUP_CREATE_SUCCESS("CREATED", "체크 그룹 생성에 성공했습니다."),
    TASK_GROUP_COLLECTION_LOAD_SUCCESS("OK", "체크 그룹 리스트 조회에 성공했습니다."),
    TASK_GROUP_TITLE_UPDATE_SUCCESS("UPDATED", "체크 그룹 제목 수정에 성공했습니다."),
    TASK_GROUP_CONTENT_UPDATE_SUCCESS("UPDATED", "체크 그룹 메모 수정에 성공했습니다."),
    TASK_GROUP_DELETE_SUCCESS("DELETED", "체크 그룹 삭제에 성공했습니다."),


    // USER
    SIGNUP_SUCCESS("CREATED", "회원가입을 성공했습니다."),
    SIGNUP_SEND_CODE_SUCCESS("OK", "인증 번호를 전송했습니다."),
    SIGNUP_VERIFY_CODE_SUCCESS("OK", "인증에 성공했습니다."),
    SIGNUP_AVAILABLE_NICKNAME_SUCCESS("OK", "사용 가능한 닉네임입니다."),
    SIGNUP_SEND_LINK_SUCCESS("OK", "인증 링크를 전송했습니다."),
    LOGIN_SUCCESS("OK", "로그인에 성공했습니다."),
    LOGIN_REISSUE_SUCCESS("OK", "accessToken 재발급을 성공했습니다."),
    LOGOUT_SUCCESS("OK", "로그아웃에 성공했습니다."),
    SOCIAL_LOGIN_SUCCESS("CREATED", "회원가입 후 로그인에 성공했습니다."),

    PASSWORD_RESET_SUCCESS("UPDATED", "비밀번호 재설정에 성공했습니다."),
    USER_NICKNAME_LOAD_SUCCESS("UPDATE", "닉네임 수정에 성공했습니다."),
    USER_NICKNAME_UPDATE_SUCCESS("UPDATE", "닉네임 수정에 성공했습니다."),
    USER_REDIS_ACCESS_SUCCESS("OK", "redis 접근에 성공했습니다."),
    EXIST_EMAIL("EXIST_EMAIL", "이미 사용중인 이메일입니다."),
    TOOMANY_REQUEST("TOOMANY_REQUEST", "5초안에 다시 요청했습니다."),
    BAD_REQUEST("BAD_REQUEST", "필수값이 누락되거나 형식이 올바르지 않습니다."),
    TIME_OUT("TIME_OUT", "시간이 초과되었습니다."),
    EXIST_NICKNAME("EXIST_NICKNAME", "이미 사용중인 닉네임입니다."),
    NOT_EXIST_EMAIL("NOT_EXIST_EMAIL", "존재하지 않는 이메일입니다."),
    SOCIAL_LOGIN("SOCIAL_LOGIN", "소셜 로그인은 비밀번호 찾기를 진행할 수 없습니다."),
    SOCIAL_LOGIN_NEEDED("SOCIAL_LOGIN_NEEDED", "소셜 로그인 회원입니다. 비밀번호 찾기를 진행할 수 없습니다.");

    private final String code;
    private final String message;

    CustomCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
} 