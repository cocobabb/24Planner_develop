package com.example.p24zip.domain.taskGroup.dto.response;

import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskGroupMemoUpdateResponseDto {

    private final Long id;
    private final String memo;

    public static TaskGroupMemoUpdateResponseDto from(TaskGroup entity){
        return TaskGroupMemoUpdateResponseDto.builder()
            .id(entity.getId())
            .memo(entity.getTitle())
            .build();
    }
}
