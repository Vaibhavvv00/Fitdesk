package com.fitdesk.repository;

import com.fitdesk.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    @Query("SELECT COUNT(DISTINCT a.member.id) FROM Attendance a WHERE DATE(a.checkIn) = CURRENT_DATE")
    long countTodayAttendance();
    
    @Query("SELECT a FROM Attendance a LEFT JOIN FETCH a.member WHERE DATE(a.checkIn) = CURRENT_DATE ORDER BY a.checkIn DESC")
    List<Attendance> findTodayAttendance();
    
    Optional<Attendance> findByMemberIdAndCheckOutIsNull(Long memberId);
}
