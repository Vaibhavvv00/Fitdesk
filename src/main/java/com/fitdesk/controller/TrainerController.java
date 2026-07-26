package com.fitdesk.controller;

import com.fitdesk.entity.Trainer;
import com.fitdesk.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@CrossOrigin
public class TrainerController {
    private final TrainerService trainerService;

    @GetMapping("")
    public ResponseEntity<List<Trainer>> getAll() {
        return ResponseEntity.ok(trainerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getById(@PathVariable Long id) {
        return ResponseEntity.ok(trainerService.getById(id));
    }

    @PostMapping("")
    public ResponseEntity<Trainer> create(@RequestBody Trainer trainer) {
        return ResponseEntity.status(201).body(trainerService.create(trainer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trainer> update(@PathVariable Long id, @RequestBody Trainer trainer) {
        return ResponseEntity.ok(trainerService.update(id, trainer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trainerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
