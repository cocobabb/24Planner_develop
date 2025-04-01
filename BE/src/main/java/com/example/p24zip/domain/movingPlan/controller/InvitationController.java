package com.example.p24zip.domain.movingPlan.controller;

import com.example.p24zip.domain.movingPlan.dto.request.HousemateInvitationRequestDto;
import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationAcceptResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationValidateResponseDto;
import com.example.p24zip.domain.movingPlan.service.HousemateService;
import com.example.p24zip.domain.movingPlan.service.InvitationService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans/invitations")
public class InvitationController {

    private final HousemateService housemateService;
    private final InvitationService invitationService;

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<HousemateInvitationValidateResponseDto>> validateInvitation(
            @RequestParam String token) {

        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "유효한 초대 링크입니다.",
                invitationService.validateInvitationToken(token)
        ));
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<HousemateInvitationAcceptResponseDto>> acceptInvitation(
            @RequestBody HousemateInvitationRequestDto requestDto,
            @AuthenticationPrincipal User user) {

        HousemateInvitationValidateResponseDto validationResult = invitationService.validateInvitationToken(requestDto.getToken());

        return ResponseEntity.ok(ApiResponse.ok(
                "ACCEPTED",
                "초대를 수락하고 동거인으로 등록되었습니다.",
                housemateService.acceptInvitation(validationResult.getMovingPlanId(), user)
        ));
    }
}
