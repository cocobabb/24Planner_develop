package com.example.p24zip.domain.taskGroup.repository;

import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long> {

    @Query("SELECT tg FROM TaskGroup tg "
        + "WHERE tg.movingPlan.id = :movingPlanId")
    List<TaskGroup> findAllByMovingPlanId(@Param("movingPlanId") Long movingPlanId);


}
