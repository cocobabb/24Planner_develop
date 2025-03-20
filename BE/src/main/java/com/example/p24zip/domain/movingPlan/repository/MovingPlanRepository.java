package com.example.p24zip.domain.movingPlan.repository;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovingPlanRepository extends JpaRepository<MovingPlan, Long> {
}
