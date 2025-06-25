package com.example.p24zip.domain.movingPlan.controller;

import com.example.p24zip.domain.movingPlan.dto.request.MovingPlanRequestDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanHousemateResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanOwnerResponseDto;
import com.example.p24zip.domain.movingPlan.dto.response.MovingPlanResponseDto;
import com.example.p24zip.domain.movingPlan.service.MovingPlanService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.response.ApiResponse;
import com.example.p24zip.global.validator.MovingPlanValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class MovingPlanController {

    private final MovingPlanService movingPlanService;
    private final MovingPlanValidator movingPlanValidator;

    @PostMapping
    public ResponseEntity<ApiResponse<MovingPlanOwnerResponseDto>> createMovingPlan(
            @Valid @RequestBody MovingPlanRequestDto requestDto,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.ok(
                "CREATED",
                "플랜이 생성되었습니다.",
                movingPlanService.createMovingPlan(requestDto, user)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<MovingPlanOwnerResponseDto>>>> readMovingPlans(
            @AuthenticationPrincipal User user) {

        List<MovingPlanOwnerResponseDto> movingPlans = movingPlanService.readMovingPlans(user);
        Map<String, List<MovingPlanOwnerResponseDto>> wrappedData = new HashMap<>();
        wrappedData.put("movingPlans", movingPlans);

        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "플랜 목록 조회에 성공했습니다.",
                wrappedData
        ));
    }

    @GetMapping("/{movingPlanId}")
    public ResponseEntity<ApiResponse<MovingPlanHousemateResponseDto>> readMovingPlanById(
            @PathVariable Long movingPlanId,
            @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "플랜 조회에 성공했습니다.",
                movingPlanService.readMovingPlanById(movingPlanId, user)
        ));
    }

    @GetMapping("/{movingPlanId}/title")
    public ResponseEntity<ApiResponse<MovingPlanResponseDto>> readMovingPlanTitleById(
            @PathVariable Long movingPlanId,
            @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "플랜 제목 조회에 성공했습니다.",
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
                "UPDATED",
                "플랜 제목 수정에 성공했습니다.",
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
                "DELETED",
                "플랜 삭제에 성공했습니다.",
                null
        ));
    }
}
