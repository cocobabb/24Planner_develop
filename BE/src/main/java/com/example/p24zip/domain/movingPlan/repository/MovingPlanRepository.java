package com.example.p24zip.domain.movingPlan.repository;

import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovingPlanRepository extends JpaRepository<MovingPlan, Long> {

}
