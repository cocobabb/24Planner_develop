package com.example.p24zip.domain.chat.repository;

import com.example.p24zip.domain.chat.entity.Chat;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Cursor messageId 기준 다음 메세지들 가져옴 
    @Query("""
            SELECT c FROM Chat c
            WHERE c.movingPlan.id = :movingPlanId
              AND c.id > :messageId
        """)
    List<Chat> findChatsAfterId(
        @Param("movingPlanId") Long movingPlanId,
        @Param("messageId") Long messageId,
        Pageable pageable);

    // Cursor에 messageId 없을 경우 처음 부터 size 50 만큼 채팅 데이터 가져오기
    @Query("""
            SELECT c FROM Chat c
            WHERE c.movingPlan.id = :movingPlanId
            ORDER BY c.id ASC
        """)
    List<Chat> findRecentChats(
        @Param("movingPlanId") Long movingPlanId,
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
