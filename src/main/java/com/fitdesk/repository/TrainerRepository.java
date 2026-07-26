package com.fitdesk.repository;

import com.fitdesk.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    long countByStatus(String status);
    List<Trainer> findByStatus(String status);
}
