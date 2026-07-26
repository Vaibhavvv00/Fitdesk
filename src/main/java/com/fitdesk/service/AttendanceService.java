package com.fitdesk.service;

import com.fitdesk.entity.Attendance;
import com.fitdesk.entity.Member;
import com.fitdesk.repository.AttendanceRepository;
import com.fitdesk.repository.MemberRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
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
        Member member = memberRepository.findById(memberId).orElseThrow();
        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setCheckIn(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long memberId) {
        Attendance attendance = attendanceRepository.findByMemberIdAndCheckOutIsNull(memberId)
            .orElseThrow(() -> new RuntimeException("No active check-in found"));
        attendance.setCheckOut(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getTodayAttendance() {
        return attendanceRepository.findTodayAttendance();
    }

    public long countToday() {
        return attendanceRepository.countTodayAttendance();
    }
}
