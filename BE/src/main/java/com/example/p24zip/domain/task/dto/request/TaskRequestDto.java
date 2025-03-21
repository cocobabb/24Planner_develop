package com.example.p24zip.domain.task.dto.request;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.task.entity.Task;
import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskRequestDto {

    @NotBlank
    private String content;

    public Task toEntity(MovingPlan movingPlan, TaskGroup taskGroup) {
        return Task.builder()
                .content(this.content)
                .movingPlan(movingPlan)
                .taskGroup(taskGroup)
                .build();
    }
}