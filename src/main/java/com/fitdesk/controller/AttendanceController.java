package com.fitdesk.controller;

import com.fitdesk.entity.Attendance;
import com.fitdesk.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody Map<String, Long> body) {
        try {
            return ResponseEntity.status(201).body(attendanceService.checkIn(body.get("memberId")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestBody Map<String, Long> body) {
        try {
            return ResponseEntity.ok(attendanceService.checkOut(body.get("memberId")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/today")
    public ResponseEntity<List<Attendance>> getToday() {
        return ResponseEntity.ok(attendanceService.getTodayAttendance());
    }
}
