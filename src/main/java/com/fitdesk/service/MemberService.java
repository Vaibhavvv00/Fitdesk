package com.fitdesk.service;

import com.fitdesk.entity.Member;
import com.fitdesk.entity.Plan;
import com.fitdesk.repository.MemberRepository;
import com.fitdesk.repository.PlanRepository;
import com.fitdesk.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final TrainerRepository trainerRepository;
    
    public List<Member> getAll() { 
        return memberRepository.findAllWithDetails(); 
    }
    
    public Member getById(Long id) { 
        return memberRepository.findByIdWithDetails(id).orElseThrow(); 
    }
    
    public Member create(Member member, Long planId, Long trainerId) {
        if(planId != null) member.setPlan(planRepository.findById(planId).orElse(null));
        if(trainerId != null) member.setTrainer(trainerRepository.findById(trainerId).orElse(null));
        if(member.getJoinDate() == null) member.setJoinDate(LocalDate.now());
        if(member.getStatus() == null) member.setStatus("ACTIVE");
        if (member.getPlan() != null && member.getPlanStartDate() == null && member.getPlanEndDate() == null) {
            applyMembershipDates(member, member.getJoinDate());
        }
        return memberRepository.save(member);
    }
    
    public Member update(Long id, Member updated, Long planId, Long trainerId) {
        Member existing = memberRepository.findById(id).orElseThrow();
        Plan oldPlan = existing.getPlan();
        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setGender(updated.getGender());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setStatus(updated.getStatus());
        existing.setPhotoUrl(updated.getPhotoUrl());
        if (updated.getPlanStartDate() != null) existing.setPlanStartDate(updated.getPlanStartDate());
        if (updated.getPlanEndDate() != null) existing.setPlanEndDate(updated.getPlanEndDate());
        if(planId != null) existing.setPlan(planRepository.findById(planId).orElse(null));
        else existing.setPlan(null);
        if(trainerId != null) existing.setTrainer(trainerRepository.findById(trainerId).orElse(null));
        else existing.setTrainer(null);
        if (existing.getPlan() != null && (oldPlan == null || !oldPlan.getId().equals(existing.getPlan().getId()))) {
            applyMembershipDates(existing, LocalDate.now());
        }
        return memberRepository.save(existing);
    }

    public void extendMembershipOnPayment(Long memberId, LocalDate paymentDate) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Plan plan = member.getPlan();
        if (plan == null) return;
        LocalDate asOf = paymentDate != null ? paymentDate : LocalDate.now();
        LocalDate currentEnd = member.getPlanEndDate();
        if (currentEnd == null || !currentEnd.isAfter(asOf)) {
            applyMembershipDates(member, asOf);
        } else {
            member.setPlanEndDate(currentEnd.plusMonths(plan.getDurationMonths()));
            memberRepository.save(member);
        }
    }

    private void applyMembershipDates(Member member, LocalDate startDate) {
        Plan plan = member.getPlan();
        if (plan == null || startDate == null) return;
        member.setPlanStartDate(startDate);
        member.setPlanEndDate(startDate.plusMonths(plan.getDurationMonths()));
    }
    
    public void delete(Long id) { 
        memberRepository.deleteById(id); 
    }
    
    public long countActive() { 
        return memberRepository.countByStatus("ACTIVE"); 
    }
}
