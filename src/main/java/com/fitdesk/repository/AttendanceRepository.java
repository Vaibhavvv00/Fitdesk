package com.fitdesk.repository;

import com.fitdesk.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT COUNT(DISTINCT a.member.id) FROM Attendance a WHERE a.checkIn >= :startOfDay AND a.checkIn <= :endOfDay")
    long countAttendanceBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT a FROM Attendance a LEFT JOIN FETCH a.member WHERE a.checkIn >= :startOfDay AND a.checkIn <= :endOfDay ORDER BY a.checkIn DESC")
    List<Attendance> findAttendanceBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    Optional<Attendance> findByMemberIdAndCheckOutIsNull(Long memberId);
}
