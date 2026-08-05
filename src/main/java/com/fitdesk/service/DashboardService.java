package com.fitdesk.service;

import com.fitdesk.repository.MemberRepository;
import com.fitdesk.repository.TrainerRepository;
import com.fitdesk.repository.PaymentRepository;
import com.fitdesk.repository.AttendanceRepository;
import com.fitdesk.repository.PlanRepository;
import com.fitdesk.entity.Member;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PlanRepository planRepository;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeMembers", memberRepository.countByStatus("ACTIVE"));
        stats.put("totalTrainers", trainerRepository.countByStatus("ACTIVE"));
        
        LocalDate now = LocalDate.now();
        BigDecimal monthlyRevenue = paymentRepository.getMonthlyRevenue(now.getMonthValue(), now.getYear());
        if (monthlyRevenue == null) monthlyRevenue = BigDecimal.ZERO;
        stats.put("monthlyRevenue", monthlyRevenue);
        
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        stats.put("todayAttendance", attendanceRepository.countAttendanceBetween(startOfDay, endOfDay));

        LocalDate inSevenDays = LocalDate.now().plusDays(7);
        stats.put("expiringSoon", memberRepository.countExpiringSoon(inSevenDays));
        stats.put("expiredMemberships", memberRepository.countExpiredMemberships());
        return stats;
    }

    public List<Map<String, Object>> getRevenueTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate currentMonth = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (int i = 5; i >= 0; i--) {
            LocalDate month = currentMonth.minusMonths(i);
            BigDecimal revenue = paymentRepository.getMonthlyRevenue(month.getMonthValue(), month.getYear());
            if (revenue == null) revenue = BigDecimal.ZERO;
            
            Map<String, Object> data = new HashMap<>();
            data.put("month", month.format(formatter));
            data.put("revenue", revenue);
            trend.add(data);
        }
        return trend;
    }

    public List<Map<String, Object>> getPlanDistribution() {
        List<Member> activeMembers = memberRepository.findAll().stream()
            .filter(m -> "ACTIVE".equals(m.getStatus()) && m.getPlan() != null)
            .collect(Collectors.toList());
            
        Map<String, Long> planCounts = activeMembers.stream()
            .collect(Collectors.groupingBy(m -> m.getPlan().getName(), Collectors.counting()));
            
        List<Map<String, Object>> distribution = new ArrayList<>();
        planCounts.forEach((plan, count) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("plan", plan);
            map.put("count", count);
            distribution.add(map);
        });
        return distribution;
    }
}
