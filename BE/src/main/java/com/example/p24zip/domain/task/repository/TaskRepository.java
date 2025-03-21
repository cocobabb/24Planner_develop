package com.example.p24zip.domain.task.repository;

import com.example.p24zip.domain.task.entity.Task;
import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTaskGroup(TaskGroup taskGroup);
  
    long countByMovingPlanId(Long movingPlanId);
  
    long countByMovingPlanIdAndIsCompletedTrue(Long movingPlanId);

    long countByTaskGroupAndIsCompletedTrue(TaskGroup taskGroup);

    long countByTaskGroup(TaskGroup taskGroup);

}
