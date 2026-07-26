package com.fitdesk.controller;

import com.fitdesk.entity.Member;
import com.fitdesk.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin
public class MemberController {
    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<List<Member>> getAll() {
        return ResponseEntity.ok(memberService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getById(id));
    }

    @PostMapping("")
    public ResponseEntity<Member> create(
            @RequestBody Map<String, Object> body,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long trainerId) {
        Member member = new Member();
        populateMember(member, body);
        if (planId == null) {
            planId = extractId(body.get("planId"));
        }
        if (trainerId == null) {
            trainerId = extractId(body.get("trainerId"));
        }
        return ResponseEntity.status(201).body(memberService.create(member, planId, trainerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> update(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long trainerId) {
        Member updated = new Member();
        populateMember(updated, body);
        if (planId == null) {
            planId = extractId(body.get("planId"));
        }
        if (trainerId == null) {
            trainerId = extractId(body.get("trainerId"));
        }
        return ResponseEntity.ok(memberService.update(id, updated, planId, trainerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void populateMember(Member member, Map<String, Object> body) {
        if (body.containsKey("fullName")) member.setFullName((String) body.get("fullName"));
        if (body.containsKey("email")) member.setEmail((String) body.get("email"));
        if (body.containsKey("phone")) member.setPhone((String) body.get("phone"));
        if (body.containsKey("gender")) member.setGender((String) body.get("gender"));
        if (body.containsKey("dateOfBirth") && body.get("dateOfBirth") != null) 
            member.setDateOfBirth(LocalDate.parse(body.get("dateOfBirth").toString()));
        if (body.containsKey("status") && body.get("status") != null) 
            member.setStatus(body.get("status").toString().toUpperCase());
        if (body.containsKey("photoUrl")) member.setPhotoUrl((String) body.get("photoUrl"));
        if (body.containsKey("planStartDate") && body.get("planStartDate") != null)
            member.setPlanStartDate(LocalDate.parse(body.get("planStartDate").toString()));
        if (body.containsKey("planEndDate") && body.get("planEndDate") != null)
            member.setPlanEndDate(LocalDate.parse(body.get("planEndDate").toString()));
    }

    private Long extractId(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            return Long.parseLong(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
