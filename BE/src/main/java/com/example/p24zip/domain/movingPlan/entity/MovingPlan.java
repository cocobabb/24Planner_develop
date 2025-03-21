package com.example.p24zip.domain.movingPlan.entity;

import com.example.p24zip.domain.movingPlan.dto.request.MovingPlanRequestDto;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
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
