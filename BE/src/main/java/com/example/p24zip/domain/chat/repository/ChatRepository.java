package com.example.p24zip.domain.chat.repository;

import com.example.p24zip.domain.chat.entity.Chat;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
            SELECT c FROM Chat c
            WHERE c.movingPlan.id = :movingPlanId
              AND c.id < :messageId
            ORDER BY c.id DESC
        """)
    List<Chat> findChatsBeforeId(
        @Param("movingPlanId") Long movingPlanId,
        @Param("messageId") Long messageId,
        Pageable pageable);

    @Modifying
    @Query("""
            DELETE
            FROM Chat c
            WHERE c.movingPlan.id = :movingPlanId
        """)
    void deleteChattingPlan(@Param("movingPlanId") Long movingPlanId);


    List<Chat> findByMovingPlanId(Long movingPlanId);
}
