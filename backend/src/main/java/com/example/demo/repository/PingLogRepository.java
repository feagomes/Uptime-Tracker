package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.PingLog;

@Repository
public interface PingLogRepository extends JpaRepository<PingLog, Long> {
    List<PingLog> findByServiceIdOrderByTimestampDesc(Long serviceId);
}