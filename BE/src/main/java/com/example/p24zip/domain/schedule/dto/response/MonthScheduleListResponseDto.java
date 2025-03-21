package com.example.p24zip.domain.schedule.dto.response;

import java.time.YearMonth;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthScheduleListResponseDto {

    private final YearMonth month;
    private final List<ScheduleResponseDto> schedules;

    public static MonthScheduleListResponseDto from(YearMonth month, List<ScheduleResponseDto> scheduleInMonth){
        return MonthScheduleListResponseDto.builder()
            .month(month)
            .schedules(scheduleInMonth)
            .build();
    }

}
