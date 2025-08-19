package com.example.p24zip.domain.movingPlan.controller;

import com.example.p24zip.domain.movingPlan.dto.response.HousemateInvitationResponseDto;
import com.example.p24zip.domain.movingPlan.service.HousemateService;
import com.example.p24zip.domain.movingPlan.service.InvitationService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.response.ApiResponse;
import com.example.p24zip.global.validator.MovingPlanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans/{movingPlanId}/housemates")
public class HousemateController {

    private final HousemateService housemateService;
    private final InvitationService invitationService;
    private final MovingPlanValidator movingPlanValidator;

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<HousemateInvitationResponseDto>> createHouseMateInvitation(
        @PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanOwnership(movingPlanId, user);

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.HOUSEMATE_CREATE_SUCCESS.getCode(),
            CustomCode.HOUSEMATE_CREATE_SUCCESS.getMessage(),
            invitationService.createHouseMateInvitation(movingPlanId, user)
        ));
    }

    @DeleteMapping("/{housemateId}")
    public ResponseEntity<ApiResponse<Object>> deleteHousemate(
        @PathVariable Long movingPlanId,
        @PathVariable Long housemateId,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        housemateService.deleteHousemate(housemateId);

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.HOUSEMATE_DELETE_SUCCESS.getCode(),
            CustomCode.HOUSEMATE_DELETE_SUCCESS.getMessage(),
            null
        ));
    }
}
