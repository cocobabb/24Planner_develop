package com.example.p24zip.domain.taskGroup.dto.response;

import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskGroupResponseDto {

    private final Long id;
    private final String title;

    public static TaskGroupResponseDto from(TaskGroup entity){
        return TaskGroupResponseDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .build();
    }
}
