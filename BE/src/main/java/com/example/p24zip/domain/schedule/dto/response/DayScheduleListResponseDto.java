package com.example.p24zip.domain.schedule.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DayScheduleListResponseDto {

    private final LocalDate date;
    private final List<DayScheduleResponseDto> schedules;

    public static DayScheduleListResponseDto from(LocalDate date, List<DayScheduleResponseDto> schedules){
        return DayScheduleListResponseDto.builder()
            .date(date)
            .schedules(schedules)
            .build();
    }
}
