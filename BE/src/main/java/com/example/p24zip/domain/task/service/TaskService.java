package com.example.p24zip.domain.task.service;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.task.dto.request.TaskCompleteRequestDto;
import com.example.p24zip.domain.task.dto.request.TaskRequestDto;
import com.example.p24zip.domain.task.dto.response.TaskListResponseDto;
import com.example.p24zip.domain.task.dto.response.TaskResponseDto;
import com.example.p24zip.domain.task.entity.Task;
import com.example.p24zip.domain.task.repository.TaskRepository;
import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import com.example.p24zip.domain.taskGroup.repository.TaskGroupRepository;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final MovingPlanRepository movingPlanRepository;
    private final TaskGroupRepository taskGroupRepository;

    @Transactional
    public TaskResponseDto createTask(Long movingPlanId, Long taskGroupId, TaskRequestDto requestDto) {
        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
                .orElseThrow(ResourceNotFoundException::new);
        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
                .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, taskGroup);

        return TaskResponseDto.from(taskRepository.save(requestDto.toEntity(movingPlan, taskGroup)));
    }

    public TaskListResponseDto readTasks(Long movingPlanId, Long taskGroupId) {
        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
                .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, taskGroup);

        List<Task> tasks = taskRepository.findByTaskGroup(taskGroup);
        String title = taskGroup.getTitle();
        long totalCount = tasks.size();
        long completeCount = taskRepository.countByTaskGroupAndIsCompletedTrue(taskGroup);
        String memo = taskGroup.getMemo() != null ? taskGroup.getMemo() : "";

        return TaskListResponseDto.from(title, totalCount, completeCount, tasks, memo);
    }

    @Transactional
    public TaskResponseDto updateTaskContent(Long movingPlanId, Long taskGroupId, Long taskId, TaskRequestDto requestDto) {
        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
                .orElseThrow(ResourceNotFoundException::new);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, taskGroup);
        isTaskGroupIdMatched(taskGroupId, task);

        task.update(requestDto);

        return TaskResponseDto.from(task);
    }

    @Transactional
    public TaskResponseDto updateTaskIsCompleted(Long movingPlanId, Long taskGroupId, Long taskId, TaskCompleteRequestDto requestDto) {
        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
                .orElseThrow(ResourceNotFoundException::new);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, taskGroup);
        isTaskGroupIdMatched(taskGroupId, task);

        task.updateIsCompleted(requestDto);

        return TaskResponseDto.from(task);
    }

    @Transactional
    public void deleteTask(Long movingPlanId, Long taskGroupId, Long taskId) {
        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
                .orElseThrow(ResourceNotFoundException::new);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, taskGroup);
        isTaskGroupIdMatched(taskGroupId, task);

        taskRepository.delete(task);
    }

    private void isMovingPlanIdMatched(Long movingPlanId, TaskGroup taskGroup) {
        if (!taskGroup.getMovingPlan().getId().equals(movingPlanId)) {
            throw new ResourceNotFoundException();
        }
    }

    private void isTaskGroupIdMatched(Long taskGroupId, Task task) {
        if (!task.getTaskGroup().getId().equals(taskGroupId)) {
            throw new ResourceNotFoundException();
        }
    }
}
