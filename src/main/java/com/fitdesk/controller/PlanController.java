package com.fitdesk.controller;

import com.fitdesk.entity.Plan;
import com.fitdesk.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin
public class PlanController {
    private final PlanService planService;

    @GetMapping("")
    public ResponseEntity<List<Plan>> getAll() {
        return ResponseEntity.ok(planService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> getById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getById(id));
    }

    @PostMapping("")
    public ResponseEntity<Plan> create(@RequestBody Plan plan) {
        return ResponseEntity.status(201).body(planService.create(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plan> update(@PathVariable Long id, @RequestBody Plan plan) {
        return ResponseEntity.ok(planService.update(id, plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
