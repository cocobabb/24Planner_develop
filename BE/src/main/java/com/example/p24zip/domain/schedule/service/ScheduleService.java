package com.example.p24zip.domain.schedule.service;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.schedule.dto.request.ScheduleRequestDto;
import com.example.p24zip.domain.schedule.dto.response.DayScheduleListResponseDto;
import com.example.p24zip.domain.schedule.dto.response.MonthScheduleListResponseDto;
import com.example.p24zip.domain.schedule.dto.response.ScheduleResponseDto;
import com.example.p24zip.domain.schedule.entity.Schedule;
import com.example.p24zip.domain.schedule.repository.ScheduleRepository;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MovingPlanRepository movingPlanRepository;

    // 할 일 생성
    @Transactional
    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto, Long movingPlanId){

        isDateValid(requestDto);

        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
            .orElseThrow(ResourceNotFoundException::new);

        Schedule newSchedule = scheduleRepository.save(requestDto.toEntity(movingPlan));

        return ScheduleResponseDto.from(newSchedule);
    }

    // 할 일 월별 조회
    public MonthScheduleListResponseDto getSchedulesInMonth(Long movingPlanId, YearMonth month){

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<Schedule> allSchedulesInMonth
            = scheduleRepository.findAllByMonth(movingPlanId, startDate, endDate);

        List<ScheduleResponseDto> schedulesInMonth
            = allSchedulesInMonth.stream().map(ScheduleResponseDto::from).toList();

        return MonthScheduleListResponseDto.from(month, schedulesInMonth);
    }

    // 할 일 날짜별 조회
    public DayScheduleListResponseDto getSchedulesInDay(Long movingPlanId, LocalDate date){

        List<Schedule> allSchedulesInDate
            = scheduleRepository.findAllByStartDate(movingPlanId, date);

        List<ScheduleResponseDto> schedulesInDate
            = allSchedulesInDate.stream().map(ScheduleResponseDto::from).toList();

        return DayScheduleListResponseDto.from(date, schedulesInDate);
    }

    // 할 일 수정
    @Transactional
    public ScheduleResponseDto updateSchedule(ScheduleRequestDto requestDto, Long scheduleId, Long movingPlanId){

        isDateValid(requestDto);

        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, schedule);

        Schedule updatedSchedule = schedule.update(requestDto);

        return ScheduleResponseDto.from(updatedSchedule);
    }

    // 할 일 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId, Long movingPlanId){

        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(ResourceNotFoundException::new);

        isMovingPlanIdMatched(movingPlanId, schedule);

        scheduleRepository.delete(schedule);
    }

    // 시작 날짜가 종료 날짜 이후인 경우
    private void isDateValid(ScheduleRequestDto requestDto){
        if(requestDto.getStartDate().isAfter(requestDto.getEndDate())){
            throw new CustomException("INVALID_DATE", "시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }
    }

    // 이사 플랜 아이디와 할 일의 이사 플랜 아이디 매칭 여부 검증
    private void isMovingPlanIdMatched(Long movingPlanId, Schedule schedule){
        if(!schedule.getMovingPlan().getId().equals(movingPlanId)){
            throw new ResourceNotFoundException();
        }
    }
}
