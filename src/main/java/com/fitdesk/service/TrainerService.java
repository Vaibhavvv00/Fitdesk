package com.fitdesk.service;

import com.fitdesk.entity.Trainer;
import com.fitdesk.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerRepository trainerRepository;
    
    public List<Trainer> getAll() { return trainerRepository.findAll(); }
    public Trainer getById(Long id) { return trainerRepository.findById(id).orElseThrow(); }
    public Trainer create(Trainer trainer) { return trainerRepository.save(trainer); }
    public Trainer update(Long id, Trainer updated) {
        Trainer existing = trainerRepository.findById(id).orElseThrow();
        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setSpecialization(updated.getSpecialization());
        existing.setSalary(updated.getSalary());
        existing.setJoinDate(updated.getJoinDate());
        existing.setStatus(updated.getStatus());
        return trainerRepository.save(existing);
    }
    public void delete(Long id) { trainerRepository.deleteById(id); }
    public long countActive() { return trainerRepository.countByStatus("ACTIVE"); }
}
