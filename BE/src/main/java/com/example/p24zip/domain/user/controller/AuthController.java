package com.example.p24zip.domain.user.controller;

import com.example.p24zip.domain.user.dto.request.LoginRequestDto;
import com.example.p24zip.domain.user.dto.request.OAuthSignupRequestDto;
import com.example.p24zip.domain.user.dto.request.SignupRequestDto;
import com.example.p24zip.domain.user.dto.request.VerifyEmailRequestCodeDto;
import com.example.p24zip.domain.user.dto.request.VerifyEmailRequestDto;
import com.example.p24zip.domain.user.dto.response.AccessTokenResponseDto;
import com.example.p24zip.domain.user.dto.response.FindPasswordResponseDto;
import com.example.p24zip.domain.user.dto.response.LoginResponseDto;
import com.example.p24zip.domain.user.dto.response.OAuthSignupResponseDto;
import com.example.p24zip.domain.user.dto.response.VerifyEmailDataResponseDto;
import com.example.p24zip.domain.user.service.AuthService;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.response.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
        @RequestBody @Valid SignupRequestDto requestDto) {
        authService.signup(requestDto);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.SIGNUP_SUCCESS.getCode(),
                CustomCode.SIGNUP_SUCCESS.getMessage(),
                null
            )
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<VerifyEmailDataResponseDto>> verifyEmail(
        @RequestBody @Valid VerifyEmailRequestDto requestDto)
        throws MessagingException, UnsupportedEncodingException {

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.SIGNUP_SEND_CODE_SUCCESS.getCode(),
                CustomCode.SIGNUP_SEND_CODE_SUCCESS.getMessage(),
                authService.sendEmail(requestDto)
            )
        );
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<ApiResponse<Void>> verifyEmailCode(
        @RequestBody @Valid VerifyEmailRequestCodeDto requestDto) {
        authService.checkCode(requestDto);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.SIGNUP_VERIFY_CODE_SUCCESS.getCode(),
                CustomCode.SIGNUP_VERIFY_CODE_SUCCESS.getMessage(),
                null
            )
        );
    }

    @GetMapping("/verify-nickname")
    public ResponseEntity<ApiResponse<Void>> checkNickname(@RequestParam String nickname) {
        authService.checkExistNickname(nickname);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.SIGNUP_AVAILABLE_NICKNAME_SUCCESS.getCode(),
                CustomCode.SIGNUP_AVAILABLE_NICKNAME_SUCCESS.getMessage(),
                null
            )
        );
    }

    @PostMapping("/verify-password")
    public ResponseEntity<ApiResponse<FindPasswordResponseDto>> findPassword(
        @RequestBody @Valid VerifyEmailRequestDto requestDto)
        throws MessagingException, UnsupportedEncodingException {

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.SIGNUP_SEND_LINK_SUCCESS.getCode(),
                CustomCode.SIGNUP_SEND_LINK_SUCCESS.getMessage(),
                authService.findPassword(requestDto)
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
        @Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.LOGIN_SUCCESS.getCode(),
                CustomCode.LOGIN_SUCCESS.getMessage(),
                authService.login(requestDto, response)
            )
        );
    }

    @GetMapping("/reissue")
    public ResponseEntity<ApiResponse<AccessTokenResponseDto>> reissue(HttpServletRequest request) {
        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.LOGIN_REISSUE_SUCCESS.getCode(),
                CustomCode.LOGIN_REISSUE_SUCCESS.getMessage(),
                authService.reissue(request)
            )
        );
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request,
        HttpServletResponse response) {
        authService.logout(request, response);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.LOGOUT_SUCCESS.getCode(),
                CustomCode.LOGOUT_SUCCESS.getMessage(),
                null
            )
        );
    }

    @GetMapping("/verify")
    public void verify() {

    }

    @PostMapping("/signup/additional-info")
    public ResponseEntity<ApiResponse<OAuthSignupResponseDto>> completeSignup(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody OAuthSignupRequestDto requestDto
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.SOCIAL_LOGIN_SUCCESS.getCode(),
                CustomCode.SOCIAL_LOGIN_SUCCESS.getMessage(),
                authService.completeSignup(request, response, requestDto)
            )
        );
    }
}
