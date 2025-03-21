package com.example.p24zip.domain.taskGroup.dto.request;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskGroupRequestDto {

    @NotBlank
    private String title;

    @Builder
    public TaskGroup toEntity(MovingPlan movingPlan){
        return TaskGroup.builder()
            .title(this.title)
            .movingPlan(movingPlan)
            .build();
    }
}
