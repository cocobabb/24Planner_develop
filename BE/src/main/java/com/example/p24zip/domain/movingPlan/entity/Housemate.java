package com.example.p24zip.domain.movingPlan.entity;

import com.example.p24zip.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Housemate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "moving_plan_id")
    private MovingPlan movingPlan;

    private Boolean isOwner = false;

    public static Housemate createOwner(User user, MovingPlan movingPlan) {
        return Housemate.builder()
                .user(user)
                .movingPlan(movingPlan)
                .isOwner(true)
                .build();
    }

    public static Housemate createHousemate(User user, MovingPlan movingPlan) {
        return Housemate.builder()
                .user(user)
                .movingPlan(movingPlan)
                .isOwner(false)
                .build();
    }

    @Builder
    public Housemate(User user, MovingPlan movingPlan, Boolean isOwner) {
        this.user = user;
        this.movingPlan = movingPlan;
        this.isOwner = isOwner;
    }
}
