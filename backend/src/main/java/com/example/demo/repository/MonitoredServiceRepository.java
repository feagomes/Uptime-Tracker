package com.example.demo.repository;

import com.example.demo.model.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
}