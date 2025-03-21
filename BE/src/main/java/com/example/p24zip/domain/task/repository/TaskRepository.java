package com.example.p24zip.domain.task.repository;

import com.example.p24zip.domain.task.entity.Task;
import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTaskGroup(TaskGroup taskGroup);

    long countByTaskGroupAndIsCompletedTrue(TaskGroup taskGroup);

    long countByTaskGroup(TaskGroup taskGroup);
}
