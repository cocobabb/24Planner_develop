package com.example.p24zip.domain.task.dto.response;

import com.example.p24zip.domain.task.entity.Task;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TaskListResponseDto {

    private String title;
    private Long totalCount;
    private Long completeCount;
    private List<TaskResponseDto> tasks;
    private String memo;

    public static TaskListResponseDto from(String title, Long totalCount, Long completeCount, List<Task> tasks, String memo) {
        return TaskListResponseDto.builder()
                .title(title)
                .totalCount(totalCount)
                .completeCount(completeCount)
                .tasks(tasks.stream()
                        .map(TaskResponseDto::from)
                        .toList())
                .memo(memo)
                .build();
    }
}
