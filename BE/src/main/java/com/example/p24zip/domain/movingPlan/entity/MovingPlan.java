package com.example.p24zip.domain.movingPlan.entity;

import com.example.p24zip.domain.house.entity.House;
import com.example.p24zip.domain.movingPlan.dto.request.MovingPlanRequestDto;
import com.example.p24zip.domain.schedule.entity.Schedule;
import com.example.p24zip.domain.task.entity.Task;
import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovingPlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "movingPlan", cascade = CascadeType.REMOVE)
    private List<TaskGroup> taskGroups = new ArrayList<>();

    @OneToMany(mappedBy = "movingPlan", cascade = CascadeType.REMOVE)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "movingPlan", cascade = CascadeType.REMOVE)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "movingPlan", cascade = CascadeType.REMOVE)
    private List<House> houses = new ArrayList<>();

    @Builder
    public MovingPlan(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public MovingPlan update(MovingPlanRequestDto requestDto) {
        this.title = requestDto.getTitle();

        return this;
    }
}
