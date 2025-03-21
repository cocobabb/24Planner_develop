package com.example.p24zip.domain.taskGroup.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskGroupListResponseDto {

    private final int totalProgress;
    private final List<TaskGroupDetailResponseDto> taskGroups;

    public static TaskGroupListResponseDto from(int totalProgress, List<TaskGroupDetailResponseDto> taskGroups){
        return TaskGroupListResponseDto.builder()
            .totalProgress(totalProgress)
            .taskGroups(taskGroups)
            .build();
    }

}
