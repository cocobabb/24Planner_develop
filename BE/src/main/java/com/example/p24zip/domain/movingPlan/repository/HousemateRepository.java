package com.example.p24zip.domain.movingPlan.repository;

import com.example.p24zip.domain.movingPlan.entity.Housemate;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousemateRepository extends JpaRepository<Housemate, Long> {

    List<Housemate> findByUserOrderByMovingPlanCreatedAtDesc(User user);

    List<Housemate> findByMovingPlan(MovingPlan movingPlan);

    Housemate findByUserAndMovingPlan(User user, MovingPlan movingPlan);

    Boolean existsByUserAndMovingPlan(User user, MovingPlan movingPlan);

    List<Housemate> findByUserAndIsOwnerTrue(User user);

}
