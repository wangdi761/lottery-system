package com.example.lottery.repository;

import com.example.lottery.entity.DrawRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrawRecordRepository extends JpaRepository<DrawRecord, Long> {
}
