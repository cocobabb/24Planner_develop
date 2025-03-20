package com.example.p24zip.domain.user.controller;

import com.example.p24zip.domain.user.dto.request.LoginRequestDto;
import com.example.p24zip.domain.user.dto.request.SignupRequestDto;
import com.example.p24zip.domain.user.dto.request.VerifyEmailRequestCodeDto;
import com.example.p24zip.domain.user.dto.request.VerifyEmailRequestDto;
import com.example.p24zip.domain.user.dto.response.VerifyEmailDataResponseDto;
import com.example.p24zip.domain.user.dto.response.AccessTokenResponseDto;
import com.example.p24zip.domain.user.dto.response.LoginResponseDto;
import com.example.p24zip.domain.user.service.AuthService;
import com.example.p24zip.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;



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
            ApiResponse.ok("CREATED", "회원가입을 성공했습니다.", null)
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<VerifyEmailDataResponseDto>> verifyEmail(@RequestBody @Valid VerifyEmailRequestDto requestDto){

        return ResponseEntity.ok(
            ApiResponse.ok("OK", "인증 번호를 전송했습니다.", authService.sendEmail(requestDto))
        );
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<ApiResponse<Void>> verifyEmailCode(@RequestBody @Valid VerifyEmailRequestCodeDto requestDto) {
        authService.checkCode(requestDto);

        return ResponseEntity.ok(
            ApiResponse.ok("OK", "인증에 성공했습니다.", null)
        );
    }

    @GetMapping("/verify-nickname")
    public ResponseEntity<ApiResponse<Void>> checkNickname(@RequestParam String nickname) {
        authService.checkExistNickname(nickname);

        return ResponseEntity.ok(
            ApiResponse.ok("OK","사용 가능한 닉네임입니다.", null)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "로그인이 성공했습니다.",
                authService.login(requestDto, response)
        ));
    }

    @GetMapping("/reissue")
    public ResponseEntity<ApiResponse<AccessTokenResponseDto>> reissue(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "accessToken 재발급을 성공했습니다.",
                authService.reissue(request)
        ));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);

        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "로그아웃을 성공했습니다.",
                null
        ));
    }

    @GetMapping("/verify")
    public void verify() {

    }
}
