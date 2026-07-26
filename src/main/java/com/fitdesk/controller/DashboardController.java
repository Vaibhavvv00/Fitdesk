package com.fitdesk.controller;

import com.fitdesk.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/revenue-trend")
    public ResponseEntity<List<Map<String, Object>>> getRevenueTrend() {
        return ResponseEntity.ok(dashboardService.getRevenueTrend());
    }

    @GetMapping("/plan-distribution")
    public ResponseEntity<List<Map<String, Object>>> getPlanDistribution() {
        return ResponseEntity.ok(dashboardService.getPlanDistribution());
    }
}
