package com.example.p24zip.domain.movingPlan.controller;

import com.example.p24zip.domain.movingPlan.dto.request.MovingPlanRequestDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanHousemateResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanOwnerResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanResponseDto;
import com.example.p24zip.domain.movingPlan.service.MovingPlanService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.response.ApiResponse;
import com.example.p24zip.global.validator.MovingPlanValidator;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class MovingPlanController {

    private final MovingPlanService movingPlanService;
    private final MovingPlanValidator movingPlanValidator;

    @PostMapping
    public ResponseEntity<ApiResponse<MovingPlanOwnerResponseDto>> createMovingPlan(
        @Valid @RequestBody MovingPlanRequestDto requestDto,
        @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.MOVING_PLAN_CREATE_SUCCESS.getCode(),
                CustomCode.MOVING_PLAN_CREATE_SUCCESS.getMessage(),
                movingPlanService.createMovingPlan(requestDto, user)
            )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<MovingPlanOwnerResponseDto>>>> readMovingPlans(
        @AuthenticationPrincipal User user) {

        List<MovingPlanOwnerResponseDto> movingPlans = movingPlanService.readMovingPlans(user);
        Map<String, List<MovingPlanOwnerResponseDto>> wrappedData = new HashMap<>();
        wrappedData.put("movingPlans", movingPlans);

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.MOVING_PLAN_COLLECTIONS_LOAD_SUCCESS.getCode(),
            CustomCode.MOVING_PLAN_COLLECTIONS_LOAD_SUCCESS.getMessage(),
            wrappedData
        ));
    }

    @GetMapping("/{movingPlanId}")
    public ResponseEntity<ApiResponse<MovingPlanHousemateResponseDto>> readMovingPlanById(
        @PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.MOVING_PLAN_LOAD_SUCCESS.getCode(),
            CustomCode.MOVING_PLAN_LOAD_SUCCESS.getMessage(),
            movingPlanService.readMovingPlanById(movingPlanId, user)
        ));
    }

    @GetMapping("/{movingPlanId}/title")
    public ResponseEntity<ApiResponse<MovingPlanResponseDto>> readMovingPlanTitleById(
        @PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.MOVING_PLAN_TITLE_LOAD_SUCCESS.getCode(),
            CustomCode.MOVING_PLAN_TITLE_LOAD_SUCCESS.getMessage(),
            movingPlanService.readMovingPlanTitleById(movingPlanId)
        ));
    }

    @PatchMapping("/{movingPlanId}/title")
    public ResponseEntity<ApiResponse<MovingPlanResponseDto>> updateMovingPlan(
        @PathVariable Long movingPlanId,
        @Valid @RequestBody MovingPlanRequestDto requestDto,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanOwnership(movingPlanId, user);

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.MOVING_PLAN_TITLE_UPDATE_SUCCESS.getCode(),
            CustomCode.MOVING_PLAN_TITLE_UPDATE_SUCCESS.getMessage(),
            movingPlanService.updateMovingPlan(movingPlanId, requestDto)
        ));
    }

    @DeleteMapping("/{movingPlanId}")
    public ResponseEntity<ApiResponse<Object>> deleteMovingPlan(
        @PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanOwnership(movingPlanId, user);

        movingPlanService.deleteMovingPlan(movingPlanId);

        return ResponseEntity.ok(ApiResponse.ok(
            CustomCode.MOVING_PLAN_DELETE_SUCCESS.getCode(),
            CustomCode.MOVING_PLAN_DELETE_SUCCESS.getMessage(),
            null
        ));
    }
}
