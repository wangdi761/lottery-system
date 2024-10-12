package com.example.lottery.repository;

import com.example.lottery.entity.DrawChance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DrawChanceRepository extends JpaRepository<DrawChance, Long> {
    Optional<DrawChance> findByUserIdAndActivityId(Long userId, Long activityId);

    @Transactional
    @Modifying
    @Query("UPDATE DrawChance dc SET dc.remainingChances = dc.remainingChances - 1 WHERE dc.userId = :userId AND dc.activityId = :activityId AND dc.remainingChances > 0")
    int decrementDrawChance(Long userId, Long activityId);
}
