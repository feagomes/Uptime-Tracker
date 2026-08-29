package com.example.demo.repository;

import com.example.demo.model.ServiceCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCheckRepository extends JpaRepository<ServiceCheck, Long> {
}