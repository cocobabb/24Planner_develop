package com.example.p24zip.domain.task.dto.response;

import com.example.p24zip.domain.task.entity.Task;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskResponseDto {

    private Long id;
    private String content;
    private Boolean isCompleted;

    public static TaskResponseDto from(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .content(task.getContent())
                .isCompleted(task.getIsCompleted())
                .build();
    }
}
