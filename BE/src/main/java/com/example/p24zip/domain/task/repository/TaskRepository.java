package com.example.p24zip.domain.task.repository;

import com.example.p24zip.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
