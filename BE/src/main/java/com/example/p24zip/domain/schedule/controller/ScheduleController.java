package com.example.p24zip.domain.schedule.controller;

import com.example.p24zip.domain.schedule.dto.request.ScheduleRequestDto;
import com.example.p24zip.domain.schedule.dto.response.DayScheduleListResponseDto;
import com.example.p24zip.domain.schedule.dto.response.MonthScheduleListResponseDto;
import com.example.p24zip.domain.schedule.dto.response.ScheduleResponseDto;
import com.example.p24zip.domain.schedule.service.ScheduleService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans/{movingPlanId}/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 할 일 생성
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> createSchedule(
        @RequestBody @Valid ScheduleRequestDto requestDto,
        @PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(ApiResponse.ok(
            "CREATED",
            "할 일 생성에 성공했습니다.",
            scheduleService.createSchedule(requestDto, movingPlanId)
        ));
    }

    // 할 일 월별 조회
    @GetMapping("/month")
    public ResponseEntity<ApiResponse<MonthScheduleListResponseDto>> getSchedulesInMonth(
        @PathVariable Long movingPlanId,
        @RequestParam YearMonth month,
        @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(ApiResponse.ok(
            "OK",
            "할 일 월별 목록 조회에 성공했습니다.",
            scheduleService.getSchedulesInMonth(movingPlanId, month)
        ));
    }

    // 할 일 날짜별 조회
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<DayScheduleListResponseDto>> getSchedulesInDay(
        @PathVariable Long movingPlanId,
        @RequestParam LocalDate date,
        @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(ApiResponse.ok(
            "OK",
            "할 일 날짜별 목록 조회에 성공했습니다.",
            scheduleService.getSchedulesInDay(movingPlanId, date)
        ));
    }

    // 할 일 수정
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> updateSchedule(
        @Valid @RequestBody ScheduleRequestDto requestDto,
        @PathVariable Long scheduleId,
        @PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user
        ){
        return ResponseEntity.ok(ApiResponse.ok(
            "UPDATED",
            "할 일 수정에 성공했습니다.",
            scheduleService.updateSchedule(requestDto, scheduleId, movingPlanId)
        ));
    }

    // 할 일 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Object>> deleteSchedule(
        @PathVariable Long scheduleId,
        @PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user
    ){
        scheduleService.deleteSchedule(scheduleId, movingPlanId);
        return ResponseEntity.ok(ApiResponse.ok(
            "DELETED",
            "할 일 삭제에 성공했습니다.",
            null
        ));
    }

}
