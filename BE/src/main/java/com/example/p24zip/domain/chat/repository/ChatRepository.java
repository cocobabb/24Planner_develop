package com.example.p24zip.domain.chat.repository;

import com.example.p24zip.domain.chat.entity.Chat;
import com.example.p24zip.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
        SELECT c FROM Chat c
        WHERE c.movingPlan.id = :movingPlanId
        ORDER BY c.createdAt ASC
    """)
    List<Chat> findAllById(@Param("movingPlanId") Long movingPlanId);

    @Modifying
    @Query("""
        DELETE
        FROM Chat c
        WHERE c.movingPlan.id = :movingPlanId
    """)
    void deleteChattingPlan(@Param("movingPlanId") Long movingPlanId);

    List<Chat> findAllByUser(User user);
}
