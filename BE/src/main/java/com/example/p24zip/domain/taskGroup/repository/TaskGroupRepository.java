package com.example.p24zip.domain.taskGroup.repository;

import com.example.p24zip.domain.taskGroup.entity.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long> {
}
