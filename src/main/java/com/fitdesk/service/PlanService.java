package com.fitdesk.service;

import com.fitdesk.entity.Plan;
import com.fitdesk.repository.PlanRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    
    public List<Plan> getAll() { return planRepository.findAll(); }
    public Plan getById(Long id) { return planRepository.findById(id).orElseThrow(); }
    public Plan create(Plan plan) { return planRepository.save(plan); }
    public Plan update(Long id, Plan updated) {
        Plan existing = planRepository.findById(id).orElseThrow();
        existing.setName(updated.getName());
        existing.setDurationMonths(updated.getDurationMonths());
        existing.setPrice(updated.getPrice());
        existing.setDescription(updated.getDescription());
        existing.setIsActive(updated.getIsActive());
        return planRepository.save(existing);
    }
    public void delete(Long id) { planRepository.deleteById(id); }
    public List<Plan> getActivePlans() {
        return planRepository.findByIsActive(true);
    }
}
