package com.example.p24zip.domain.taskGroup.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class TaskGroupMemoUpdateRequestDto {

    @Length(max = 1000)
    private String memo;
}
