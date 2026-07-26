package com.fitdesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 15)
    private String phone;

    @Column(length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "plan_start_date")
    private LocalDate planStartDate;

    @Column(name = "plan_end_date")
    private LocalDate planEndDate;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Trainer trainer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
