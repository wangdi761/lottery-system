package com.example.lottery.repository;

import com.example.lottery.entity.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PrizeRepository extends JpaRepository<Prize, Long> {
    List<Prize> findByActivityId(Long activityId);

    @Transactional
    @Modifying
    @Query("UPDATE Prize p SET p.stock = p.stock - 1 WHERE p.id = :prizeId AND p.stock > 0")
    int decrementStock(Long prizeId);
}
