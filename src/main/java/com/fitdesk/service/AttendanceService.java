package com.fitdesk.service;

import com.fitdesk.entity.Attendance;
import com.fitdesk.entity.Member;
import com.fitdesk.repository.AttendanceRepository;
import com.fitdesk.repository.MemberRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    public Attendance checkIn(Long memberId) {
        if (attendanceRepository.findByMemberIdAndCheckOutIsNull(memberId).isPresent()) {
            throw new RuntimeException("Member is already checked in");
        }
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setCheckIn(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long memberId) {
        Attendance attendance = attendanceRepository.findByMemberIdAndCheckOutIsNull(memberId)
            .orElseThrow(() -> new RuntimeException("No active check-in found for this member"));
        attendance.setCheckOut(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getTodayAttendance() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return attendanceRepository.findAttendanceBetween(startOfDay, endOfDay);
    }

    public long countToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return attendanceRepository.countAttendanceBetween(startOfDay, endOfDay);
    }
}
