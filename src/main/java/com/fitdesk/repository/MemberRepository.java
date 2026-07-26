package com.fitdesk.repository;

import com.fitdesk.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    long countByStatus(String status);
    List<Member> findByStatus(String status);
    
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.plan LEFT JOIN FETCH m.trainer")
    List<Member> findAllWithDetails();
    
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.plan LEFT JOIN FETCH m.trainer WHERE m.id = :id")
    Optional<Member> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.status = 'ACTIVE' AND m.planEndDate IS NOT NULL AND m.planEndDate < CURRENT_DATE")
    long countExpiredMemberships();

    @Query("SELECT COUNT(m) FROM Member m WHERE m.status = 'ACTIVE' AND m.planEndDate IS NOT NULL AND m.planEndDate >= CURRENT_DATE AND m.planEndDate <= :deadline")
    long countExpiringSoon(@Param("deadline") LocalDate deadline);
}
