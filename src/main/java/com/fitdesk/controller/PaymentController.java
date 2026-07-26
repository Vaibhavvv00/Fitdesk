package com.fitdesk.controller;

import com.fitdesk.entity.Payment;
import com.fitdesk.service.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("")
    public ResponseEntity<List<Payment>> getAll(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {
        if (memberId != null || fromDate != null || toDate != null) {
            return ResponseEntity.ok(paymentService.getFiltered(memberId, fromDate, toDate));
        }
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {
        List<Payment> payments = paymentService.getFiltered(memberId, fromDate, toDate);
        StringBuilder csv = new StringBuilder("Member,Amount,Date,Method,Status\n");
        for (Payment p : payments) {
            String memberName = p.getMember() != null ? escapeCsv(p.getMember().getFullName()) : "Unknown";
            csv.append(memberName).append(',')
               .append(p.getAmount()).append(',')
               .append(p.getPaymentDate()).append(',')
               .append(p.getPaymentMethod()).append(',')
               .append(p.getStatus()).append('\n');
        }
        byte[] body = csv.toString().getBytes(StandardCharsets.UTF_8);
        String filename = "payments-" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @PostMapping("")
    public ResponseEntity<Payment> create(@RequestBody Map<String, Object> body) {
        Payment payment = mapToPayment(new Payment(), body);
        Long memberId = extractMemberId(body);
        return ResponseEntity.status(201).body(paymentService.create(payment, memberId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Payment payment = mapToPayment(new Payment(), body);
        Long memberId = extractMemberId(body);
        return ResponseEntity.ok(paymentService.update(id, payment, memberId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Payment mapToPayment(Payment payment, Map<String, Object> body) {
        if (body.containsKey("amount")) payment.setAmount(new BigDecimal(body.get("amount").toString()));
        if (body.containsKey("paymentMethod") && body.get("paymentMethod") != null)
            payment.setPaymentMethod(body.get("paymentMethod").toString().toUpperCase());
        if (body.containsKey("status") && body.get("status") != null)
            payment.setStatus(body.get("status").toString().toUpperCase());
        if (body.containsKey("paymentDate") && body.get("paymentDate") != null)
            payment.setPaymentDate(LocalDate.parse(body.get("paymentDate").toString()));
        return payment;
    }

    private Long extractMemberId(Map<String, Object> body) {
        Object mIdObj = body.get("memberId");
        if (mIdObj == null) return null;
        if (mIdObj instanceof Number) return ((Number) mIdObj).longValue();
        return Long.parseLong(mIdObj.toString());
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
