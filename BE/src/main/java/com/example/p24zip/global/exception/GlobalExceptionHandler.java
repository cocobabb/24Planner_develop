package com.example.p24zip.global.exception;

import com.example.p24zip.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("NOT_FOUND", ex.getMessage()));
    }

    /**
     * 이미 사용되고 있는 이메일 아이디가 있을 때
     **/
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomExistEmail(CustomException ex) {
//    public ResponseEntity<Void> handleCustomExistEmail(ExistEmailException ex) {
//        ResponseEntity<Void> ResponseEntity = null;
//        return ResponseEntity;
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * 요청된 데이터에 빈 값이 있을 때
     **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
        MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getDefaultMessage())
            // .findFirst() : 여러 개의 필드가 유효성 검사에 실패했을 경우 첫 번째 에러만 가져옴
            .findFirst()
            .orElse("잘못된 요청입니다.");

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("BAD_REQUEST", errorMessage));
    }


}

