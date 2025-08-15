package com.example.p24zip.domain.task.controller;

import com.example.p24zip.domain.task.dto.request.TaskCompleteRequestDto;
import com.example.p24zip.domain.task.dto.request.TaskRequestDto;
import com.example.p24zip.domain.task.dto.response.TaskListResponseDto;
import com.example.p24zip.domain.task.dto.response.TaskResponseDto;
import com.example.p24zip.domain.task.service.TaskService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.response.ApiResponse;
import com.example.p24zip.global.validator.MovingPlanValidator;
import jakarta.validation.Valid;
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
@RequestMapping("/plans/{movingPlanId}/taskgroups/{taskGroupId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final MovingPlanValidator movingPlanValidator;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponseDto>> createTask(
        @PathVariable Long movingPlanId,
        @PathVariable Long taskGroupId,
        @Valid @RequestBody TaskRequestDto requestDto,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.TASK_CREATE_SUCCESS.getCode(),
                CustomCode.TASK_CREATE_SUCCESS.getMessage(),
                taskService.createTask(movingPlanId, taskGroupId, requestDto)
            )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TaskListResponseDto>> readTasks(
        @PathVariable Long movingPlanId,
        @PathVariable Long taskGroupId,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.TASK_COLLECTION_LOAD_SUCCESS.getCode(),
                CustomCode.TASK_COLLECTION_LOAD_SUCCESS.getMessage(),
                taskService.readTasks(movingPlanId, taskGroupId)
            )
        );
    }

    @PatchMapping("/{taskId}/content")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTaskContent(
        @PathVariable Long movingPlanId,
        @PathVariable Long taskGroupId,
        @PathVariable Long taskId,
        @Valid @RequestBody TaskRequestDto requestDto,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.TASK_CONTENT_UPDATE_SUCCESS.getCode(),
                CustomCode.TASK_CONTENT_UPDATE_SUCCESS.getMessage(),
                taskService.updateTaskContent(movingPlanId, taskGroupId, taskId, requestDto)
            )
        );
    }

    @PatchMapping("/{taskId}/isCompleted")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTaskIsCompleted(
        @PathVariable Long movingPlanId,
        @PathVariable Long taskGroupId,
        @PathVariable Long taskId,
        @Valid @RequestBody TaskCompleteRequestDto requestDto,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.TASK_COMPLETE_UPDATE_SUCCESS.getCode(),
                CustomCode.TASK_COMPLETE_UPDATE_SUCCESS.getMessage(),
                taskService.updateTaskIsCompleted(movingPlanId, taskGroupId, taskId, requestDto)
            )
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Object>> deleteTask(
        @PathVariable Long movingPlanId,
        @PathVariable Long taskGroupId,
        @PathVariable Long taskId,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        taskService.deleteTask(movingPlanId, taskGroupId, taskId);

        return ResponseEntity.ok(
            ApiResponse.ok(
                CustomCode.TASK_DELETE_SUCCESS.getCode(),
                CustomCode.TASK_DELETE_SUCCESS.getMessage(),
                null
            )
        );
    }
}
