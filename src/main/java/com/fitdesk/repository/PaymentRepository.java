package com.fitdesk.repository;

import com.fitdesk.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p WHERE p.status = 'COMPLETED' AND MONTH(p.paymentDate) = :month AND YEAR(p.paymentDate) = :year")
    BigDecimal getMonthlyRevenue(@Param("month") int month, @Param("year") int year);
    
    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.member ORDER BY p.paymentDate DESC")
    List<Payment> findAllWithMember();
    
    List<Payment> findTop10ByOrderByPaymentDateDesc();
    
    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.member ORDER BY p.paymentDate DESC LIMIT 10")
    List<Payment> findRecentWithMember();

    @Query("""
        SELECT p FROM Payment p LEFT JOIN FETCH p.member
        WHERE (:memberId IS NULL OR p.member.id = :memberId)
          AND (:fromDate IS NULL OR p.paymentDate >= :fromDate)
          AND (:toDate IS NULL OR p.paymentDate <= :toDate)
        ORDER BY p.paymentDate DESC
        """)
    List<Payment> findFiltered(
        @Param("memberId") Long memberId,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );
}
