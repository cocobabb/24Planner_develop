package com.example.p24zip.domain.user.controller;

import com.example.p24zip.domain.house.dto.response.ShowNicknameResponseDto;
import com.example.p24zip.domain.user.dto.request.ChangeNicknameRequestDto;
import com.example.p24zip.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.p24zip.domain.user.dto.request.LoginRequestDto;
import com.example.p24zip.domain.user.dto.request.OAuthSignupRequestDto;
import com.example.p24zip.domain.user.dto.request.SignupRequestDto;
import com.example.p24zip.domain.user.dto.request.VerifyEmailRequestCodeDto;
import com.example.p24zip.domain.user.dto.request.VerifyEmailRequestDto;
import com.example.p24zip.domain.user.dto.response.OAuthSignupResponseDto;
import com.example.p24zip.domain.user.dto.response.ChangeNicknameResponseDto;
import com.example.p24zip.domain.user.dto.response.FindPasswordResponseDto;
import com.example.p24zip.domain.user.dto.response.RedisValueResponseDto;
import com.example.p24zip.domain.user.dto.response.VerifyEmailDataResponseDto;
import com.example.p24zip.domain.user.dto.response.AccessTokenResponseDto;
import com.example.p24zip.domain.user.dto.response.LoginResponseDto;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.service.AuthService;
import com.example.p24zip.global.response.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ApiResponse<VerifyEmailDataResponseDto>> verifyEmail(@RequestBody @Valid VerifyEmailRequestDto requestDto)
        throws MessagingException, UnsupportedEncodingException {

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

    @PostMapping("/verify-password")
    public ResponseEntity<ApiResponse<FindPasswordResponseDto>> findPassword(@RequestBody @Valid VerifyEmailRequestDto requestDto)
        throws MessagingException, UnsupportedEncodingException {

        return ResponseEntity.ok(
            ApiResponse.ok("OK", "인증 링크를 전송했습니다.", authService.findPassword(requestDto))
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody @Valid ChangePasswordRequestDto requestDto,HttpServletResponse response ,@AuthenticationPrincipal User user) {
        authService.updatePassword(requestDto, response, user);

        return ResponseEntity.ok(
            ApiResponse.ok("UPDATED","비밀번호 수정에 성공했습니다.", null)
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

    @GetMapping("/nickname")
    public ResponseEntity<ApiResponse<ShowNicknameResponseDto>> getNickname(@AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
            "닉네임 조회에 성공했습니다.",
            authService.getNickname(user)
            )
        );
    }

    @GetMapping("/redis/{key}")
    public ResponseEntity<ApiResponse<RedisValueResponseDto>> getRedisValue(@PathVariable String key){

        return ResponseEntity.ok(ApiResponse.ok(
            "OK",
            "redis 접근에 성공했습니다.",
            authService.getRedisValue(key)
        ));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<ChangeNicknameResponseDto>> updateNickname(@RequestBody @Valid ChangeNicknameRequestDto requestDto, @AuthenticationPrincipal User user){

        return ResponseEntity.ok(ApiResponse.ok(
            "UPDATED",
            "닉네임 수정에 성공했습니다.",
            authService.updateNickname(requestDto, user)
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

    @PostMapping("/signup/additional-info")
    public ResponseEntity<ApiResponse<OAuthSignupResponseDto>> completeSignup(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody OAuthSignupRequestDto requestDto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            "CREATED",
            "회원가입 후 로그인에 성공했습니다.",
            authService.completeSignup(request, response, requestDto)
        ));
    }
}
