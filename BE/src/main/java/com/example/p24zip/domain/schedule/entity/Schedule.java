package com.example.p24zip.domain.schedule.entity;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.schedule.dto.request.ScheduleRequestDto;
import com.example.p24zip.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "moving_plan_id")
    private MovingPlan movingPlan;

    @Builder
    public Schedule(String content, LocalDate startDate, LocalDate endDate, String color, MovingPlan movingPlan){
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.movingPlan = movingPlan;
    }

    public Schedule update(ScheduleRequestDto requestDto){
        this.content = requestDto.getContent();
        this.startDate = requestDto.getStartDate();
        this.endDate = requestDto.getEndDate();
        this.color = requestDto.getColor();

        return this;
    }
}
