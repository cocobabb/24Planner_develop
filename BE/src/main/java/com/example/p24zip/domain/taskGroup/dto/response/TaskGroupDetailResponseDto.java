package com.example.p24zip.domain.taskGroup.dto.response;

import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskGroupDetailResponseDto {

    private final Long id;
    private final String title;
    private final int progress;

    public static TaskGroupDetailResponseDto from(TaskGroup entity, int progress){
        return TaskGroupDetailResponseDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .progress(progress)
            .build();
    }

}
