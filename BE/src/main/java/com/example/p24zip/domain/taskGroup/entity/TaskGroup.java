package com.example.p24zip.domain.taskGroup.entity;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.task.entity.Task;
import com.example.p24zip.domain.taskGroup.dto.request.TaskGroupMemoUpdateRequestDto;
import com.example.p24zip.domain.taskGroup.dto.request.TaskGroupRequestDto;
import com.example.p24zip.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskGroup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String memo;

    @ManyToOne
    @JoinColumn(name= "moving_plan_id")
    private MovingPlan movingPlan;

    @OneToMany(mappedBy="taskGroup")
    private List<Task> tasks = new ArrayList<>();

    @Builder
    public TaskGroup(String title, MovingPlan movingPlan){
        this.title = title;
        this.movingPlan = movingPlan;
    }

    public TaskGroup updateTitle(TaskGroupRequestDto requestDto){
        this.title = requestDto.getTitle();

        return this;
    }

    public TaskGroup updateMemo(TaskGroupMemoUpdateRequestDto requestDto){
        this.memo = requestDto.getMemo();

        return this;
    }
}
