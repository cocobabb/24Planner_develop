package com.example.p24zip.domain.house.repository;

import com.example.p24zip.domain.house.entity.House;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository  extends JpaRepository<House, Long> {
    List<House> findAllByMovingPlan(MovingPlan movingPlan);
}
