package com.example.p24zip.domain.schedule.repository;

import com.example.p24zip.domain.schedule.entity.Schedule;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s "
        + "WHERE s.movingPlan.id = :movingPlanId")
    List<Schedule> findAllByMovingPlanId(@Param("movingPlanId") Long movingPlanId);

}
