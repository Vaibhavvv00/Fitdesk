package com.fitdesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 15)
    private String phone;

    @Column(length = 100)
    private String specialization;

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
