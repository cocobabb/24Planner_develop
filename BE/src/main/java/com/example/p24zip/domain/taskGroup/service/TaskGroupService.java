package com.example.p24zip.domain.taskGroup.service;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.task.repository.TaskRepository;
import com.example.p24zip.domain.taskGroup.dto.request.TaskGroupMemoUpdateRequestDto;
import com.example.p24zip.domain.taskGroup.dto.request.TaskGroupRequestDto;
import com.example.p24zip.domain.taskGroup.dto.response.TaskGroupDetailResponseDto;
import com.example.p24zip.domain.taskGroup.dto.response.TaskGroupListResponseDto;
import com.example.p24zip.domain.taskGroup.dto.response.TaskGroupMemoUpdateResponseDto;
import com.example.p24zip.domain.taskGroup.dto.response.TaskGroupResponseDto;
import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import com.example.p24zip.domain.taskGroup.repository.TaskGroupRepository;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskGroupService {

    private final TaskGroupRepository taskGroupRepository;
    private final MovingPlanRepository movingPlanRepository;
    private final TaskRepository taskRepository;

    // 체크 그룹 생성
    @Transactional
    public TaskGroupResponseDto createTaskGroup(TaskGroupRequestDto requestDto, Long movingPlanId){

        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
            .orElseThrow(ResourceNotFoundException::new);

        TaskGroup newTaskGroup = taskGroupRepository.save(requestDto.toEntity(movingPlan));

        return TaskGroupResponseDto.from(newTaskGroup);
    }

    // 체크 그룹 전체 조회
    public TaskGroupListResponseDto getTaskGroups(Long movingPlanId){

        // 이사 전체 진행률 구하기
        long totalTasksCount = taskRepository.countByMovingPlanId(movingPlanId);
        long completedTasksCount = taskRepository.countByMovingPlanIdAndIsCompletedTrue(movingPlanId);

        int totalProgress = calculateCompletionPercentage(completedTasksCount, totalTasksCount);

        // 체크 그룹별 진행률 구하기
        List<TaskGroup> taskGroups = taskGroupRepository.findAllByMovingPlanId(movingPlanId);
        List<TaskGroupDetailResponseDto> taskGroupDetailResponseDtoList
            = taskGroups.stream().map(taskGroup ->
        {
            long totalTasksInTaskGroup = taskRepository.countByTaskGroup(taskGroup);
            long completedTasksInTaskGroup = taskRepository.countByTaskGroupAndIsCompletedTrue(taskGroup);

            int totalProgressInTaskGroup = calculateCompletionPercentage(completedTasksInTaskGroup,
                totalTasksInTaskGroup);

            return TaskGroupDetailResponseDto.from(taskGroup, totalProgressInTaskGroup);
        }).toList();

        return TaskGroupListResponseDto.from(totalProgress, taskGroupDetailResponseDtoList);
    }

    // 퍼센트 계산
    private int calculateCompletionPercentage(long completedTasksCount, long totalTasksCount) {

        // 작성한 체크포인트가 없는 경우
        if (totalTasksCount == 0) {
            return 0;
        }

        return (int) Math.round((double) completedTasksCount / totalTasksCount * 100);
    }

    // 체크 그룹 제목 수정
    @Transactional
    public TaskGroupResponseDto updateTaskGroupTitle(TaskGroupRequestDto requestDto, Long taskGroupId, Long movingPlanId){

        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
            .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, taskGroup);

        TaskGroup newTaskGroup = taskGroup.updateTitle(requestDto);

        return TaskGroupResponseDto.from(newTaskGroup);
    }

    // 체크 그룹 메모 수정
    @Transactional
    public TaskGroupMemoUpdateResponseDto updateTaskGroupMemo(TaskGroupMemoUpdateRequestDto requestDto, Long taskGroupId, Long movingPlanId){

        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
            .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, taskGroup);

        TaskGroup newTaskGroup = taskGroup.updateMemo(requestDto);

        return TaskGroupMemoUpdateResponseDto.from(newTaskGroup);
    }

    // 체크 그룹 삭제
    @Transactional
    public void deleteTaskGroup(Long taskGroupId, Long movingPlanId){

        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
            .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, taskGroup);

        taskGroupRepository.delete(taskGroup);
    }

    // 이사 플랜 아이디와 체크 그룹의 이사 플랜 아이디 매칭 여부 검증
    private void isMovingPlanIdMatched(Long movingPlanId, TaskGroup taskGroup){
        if(!taskGroup.getMovingPlan().getId().equals(movingPlanId)){
            throw new ResourceNotFoundException();
        }
    }
}
