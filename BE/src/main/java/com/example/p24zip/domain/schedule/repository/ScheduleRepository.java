package com.example.p24zip.domain.schedule.repository;

import com.example.p24zip.domain.schedule.entity.Schedule;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s "
        + "WHERE s.movingPlan.id = :movingPlanId "
        + "AND (s.startDate <= :endDate AND s.endDate >= :startDate)")
    List<Schedule> findAllByMonth(
        @Param("movingPlanId") Long movingPlanId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT s FROM Schedule s "
        + "WHERE s.movingPlan.id = :movingPlanId "
        + "AND (s.startDate <= :startDate AND s.endDate >= :startDate) ")
    List<Schedule> findAllByStartDate(
        @Param("movingPlanId") Long movingPlanId,
        @Param("startDate") LocalDate startDate
    );
}
