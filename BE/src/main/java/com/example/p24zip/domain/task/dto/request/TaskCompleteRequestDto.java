package com.example.p24zip.domain.task.dto.request;

import com.example.p24zip.domain.task.entity.Task;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskCompleteRequestDto {

    @NotNull
    private Boolean isCompleted;

}
