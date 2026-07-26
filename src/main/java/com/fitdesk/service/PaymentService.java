package com.fitdesk.service;

import com.fitdesk.entity.Payment;
import com.fitdesk.repository.PaymentRepository;
import com.fitdesk.repository.MemberRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    
    public List<Payment> getAll() { return paymentRepository.findAllWithMember(); }

    public List<Payment> getFiltered(Long memberId, LocalDate fromDate, LocalDate toDate) {
        return paymentRepository.findFiltered(memberId, fromDate, toDate);
    }

    public Payment getById(Long id) { return paymentRepository.findById(id).orElseThrow(); }
    
    public Payment create(Payment payment, Long memberId) {
        payment.setMember(memberRepository.findById(memberId).orElseThrow());
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        Payment saved = paymentRepository.save(payment);
        if ("COMPLETED".equalsIgnoreCase(saved.getStatus())) {
            memberService.extendMembershipOnPayment(memberId, saved.getPaymentDate());
        }
        return saved;
    }

    public Payment update(Long id, Payment updated, Long memberId) {
        Payment existing = paymentRepository.findById(id).orElseThrow();
        if (memberId != null) {
            existing.setMember(memberRepository.findById(memberId).orElseThrow());
        }
        if (updated.getAmount() != null) existing.setAmount(updated.getAmount());
        if (updated.getPaymentDate() != null) existing.setPaymentDate(updated.getPaymentDate());
        if (updated.getPaymentMethod() != null) existing.setPaymentMethod(updated.getPaymentMethod());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        return paymentRepository.save(existing);
    }
    
    public void delete(Long id) { paymentRepository.deleteById(id); }
    public List<Payment> getRecent() { return paymentRepository.findRecentWithMember(); }
}
