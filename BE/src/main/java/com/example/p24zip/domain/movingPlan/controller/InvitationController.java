package com.example.p24zip.domain.movingPlan.controller;

import com.example.p24zip.domain.movingPlan.dto.request.HousemateInvitationRequestDto;
import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationAcceptResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationValidateResponseDto;
import com.example.p24zip.domain.movingPlan.service.HousemateService;
import com.example.p24zip.domain.movingPlan.service.InvitationService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans/invitations")
public class InvitationController {

    private final HousemateService housemateService;
    private final InvitationService invitationService;

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<HousemateInvitationValidateResponseDto>> validateInvitation(
        @RequestParam String code) {

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.INVITATION_VALIDATE_SUCCESS.getCode(),
            CustomCode.INVITATION_VALIDATE_SUCCESS.getMessage(),
            invitationService.validateInvitationCode(code)
        ));
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<HousemateInvitationAcceptResponseDto>> acceptInvitation(
        @RequestBody HousemateInvitationRequestDto requestDto,
        @AuthenticationPrincipal User user) {

        HousemateInvitationValidateResponseDto validationResult = invitationService.validateInvitationCode(
            requestDto.getCode());

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.INVITATION_ACCEPTED_SUCCESS.getCode(),
            CustomCode.INVITATION_ACCEPTED_SUCCESS.getMessage(),
            housemateService.acceptInvitation(validationResult.getMovingPlanId(), user)
        ));
    }
}
