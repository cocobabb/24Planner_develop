package com.example.p24zip.domain.task.controller;

import com.example.p24zip.domain.task.dto.request.TaskCompleteRequestDto;
import com.example.p24zip.domain.task.dto.request.TaskRequestDto;
import com.example.p24zip.domain.task.dto.response.TaskListResponseDto;
import com.example.p24zip.domain.task.dto.response.TaskResponseDto;
import com.example.p24zip.domain.task.service.TaskService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans/{movingPlanId}/taskgroups/{taskGroupId}/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponseDto>> createTask(
            @PathVariable Long movingPlanId,
            @PathVariable Long taskGroupId,
            @Valid @RequestBody TaskRequestDto requestDto,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.ok(
                "CREATED",
                "체크포인트 생성에 성공했습니다.",
                taskService.createTask(movingPlanId, taskGroupId, requestDto)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TaskListResponseDto>> readTasks(
            @PathVariable Long movingPlanId,
            @PathVariable Long taskGroupId,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.ok(
                "OK",
                "체크포인트 목록 조회에 성공했습니다.",
                taskService.readTasks(movingPlanId, taskGroupId)
        ));
    }

    @PatchMapping("/{taskId}/content")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTaskContent(
            @PathVariable Long movingPlanId,
            @PathVariable Long taskGroupId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequestDto requestDto,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.ok(
           "UPDATED",
                "체크포인트 내용 수정에 성공했습니다.",
                taskService.updateTaskContent(movingPlanId, taskGroupId, taskId, requestDto)
        ));
    }

    @PatchMapping("/{taskId}/isCompleted")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTaskIsCompleted(
            @PathVariable Long movingPlanId,
            @PathVariable Long taskGroupId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskCompleteRequestDto requestDto,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.ok(
                "UPDATED",
                "체크포인트 완료 여부 수정에 성공했습니다.",
                taskService.updateTaskIsCompleted(movingPlanId, taskGroupId, taskId, requestDto)
        ));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Object>> deleteTask(
            @PathVariable Long movingPlanId,
            @PathVariable Long taskGroupId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal User user) {

        taskService.deleteTask(movingPlanId, taskGroupId, taskId);

        return ResponseEntity.ok(ApiResponse.ok(
                "DELETED",
                "체크포인트 삭제에 성공했습니다.",
                null
        ));
    }
}
