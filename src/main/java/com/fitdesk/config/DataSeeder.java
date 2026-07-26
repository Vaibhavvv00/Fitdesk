package com.fitdesk.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.fitdesk.entity.*;
import com.fitdesk.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PlanRepository planRepository;
    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (adminUserRepository.count() == 0) {
            seedData();
            System.out.println("✅ FitDesk sample data seeded successfully!");
        } else {
            // Rename Elite to Advance in existing plans if any
            planRepository.findAll().forEach(plan -> {
                if ("Elite".equalsIgnoreCase(plan.getName())) {
                    plan.setName("Advance");
                    planRepository.save(plan);
                }
            });
            // Ensure additional Indian trainers exist
            seedTrainerIfNotExists("Vikram Malhotra", "vikram@fitdesk.com", "Strength & Conditioning", 36000);
            seedTrainerIfNotExists("Neha Kapoor", "neha@fitdesk.com", "Pilates & Core", 31000);
            seedTrainerIfNotExists("Rohan Joshi", "rohan@fitdesk.com", "Zumba & Cardio", 29000);
            seedTrainerIfNotExists("Ananya Iyer", "ananya@fitdesk.com", "Nutrition & Dietetics", 34000);
            backfillMembershipDates();
        }
    }

    private void backfillMembershipDates() {
        memberRepository.findAll().forEach(m -> {
            if (m.getPlan() != null && m.getPlanEndDate() == null) {
                LocalDate start = m.getJoinDate() != null ? m.getJoinDate() : LocalDate.now();
                m.setPlanStartDate(start);
                m.setPlanEndDate(start.plusMonths(m.getPlan().getDurationMonths()));
                memberRepository.save(m);
            }
        });
    }

    private void seedTrainerIfNotExists(String fullName, String email, String specialization, int salary) {
        boolean exists = trainerRepository.findAll().stream().anyMatch(t -> t.getEmail().equalsIgnoreCase(email));
        if (!exists) {
            Trainer t = new Trainer();
            t.setFullName(fullName);
            t.setEmail(email);
            t.setSpecialization(specialization);
            t.setSalary(new BigDecimal(salary));
            t.setJoinDate(LocalDate.now().minusMonths(6));
            t.setStatus("ACTIVE");
            trainerRepository.save(t);
        }
    }

    private void seedData() {
        // 1. Admin User
        AdminUser admin = new AdminUser();
        admin.setUsername("admin");
        admin.setEmail("admin@fitdesk.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setFullName("Admin User");
        admin.setRole("ADMIN");
        adminUserRepository.save(admin);

        // 2. Plans
        Plan basic = new Plan();
        basic.setName("Basic");
        basic.setDurationMonths(1);
        basic.setPrice(new BigDecimal("999.00"));
        basic.setDescription("Access to gym floor and basic equipment");
        basic.setIsActive(true);
        planRepository.save(basic);

        Plan pro = new Plan();
        pro.setName("Pro");
        pro.setDurationMonths(3);
        pro.setPrice(new BigDecimal("2499.00"));
        pro.setDescription("Full gym access + group classes + locker");
        pro.setIsActive(true);
        planRepository.save(pro);

        Plan elite = new Plan();
        elite.setName("Advance");
        elite.setDurationMonths(12);
        elite.setPrice(new BigDecimal("7999.00"));
        elite.setDescription("Everything in Pro + personal trainer + nutrition plan");
        elite.setIsActive(true);
        planRepository.save(elite);

        List<Plan> plans = List.of(basic, pro, elite);

        // 3. Trainers
        Trainer t1 = new Trainer(); t1.setFullName("Rahul Sharma"); t1.setEmail("rahul@fitdesk.com"); t1.setSpecialization("Weight Training"); t1.setSalary(new BigDecimal("35000")); t1.setJoinDate(LocalDate.now().minusMonths(12)); t1.setStatus("ACTIVE"); trainerRepository.save(t1);
        Trainer t2 = new Trainer(); t2.setFullName("Priya Singh"); t2.setEmail("priya@fitdesk.com"); t2.setSpecialization("Yoga & Flexibility"); t2.setSalary(new BigDecimal("30000")); t2.setJoinDate(LocalDate.now().minusMonths(10)); t2.setStatus("ACTIVE"); trainerRepository.save(t2);
        Trainer t3 = new Trainer(); t3.setFullName("Amit Patel"); t3.setEmail("amit@fitdesk.com"); t3.setSpecialization("CrossFit"); t3.setSalary(new BigDecimal("38000")); t3.setJoinDate(LocalDate.now().minusMonths(8)); t3.setStatus("ACTIVE"); trainerRepository.save(t3);
        Trainer t4 = new Trainer(); t4.setFullName("Sneha Reddy"); t4.setEmail("sneha@fitdesk.com"); t4.setSpecialization("Cardio & HIIT"); t4.setSalary(new BigDecimal("32000")); t4.setJoinDate(LocalDate.now().minusMonths(5)); t4.setStatus("ACTIVE"); trainerRepository.save(t4);
        Trainer t5 = new Trainer(); t5.setFullName("Vikram Malhotra"); t5.setEmail("vikram@fitdesk.com"); t5.setSpecialization("Strength & Conditioning"); t5.setSalary(new BigDecimal("36000")); t5.setJoinDate(LocalDate.now().minusMonths(4)); t5.setStatus("ACTIVE"); trainerRepository.save(t5);
        Trainer t6 = new Trainer(); t6.setFullName("Neha Kapoor"); t6.setEmail("neha@fitdesk.com"); t6.setSpecialization("Pilates & Core"); t6.setSalary(new BigDecimal("31000")); t6.setJoinDate(LocalDate.now().minusMonths(3)); t6.setStatus("ACTIVE"); trainerRepository.save(t6);
        Trainer t7 = new Trainer(); t7.setFullName("Rohan Joshi"); t7.setEmail("rohan@fitdesk.com"); t7.setSpecialization("Zumba & Cardio"); t7.setSalary(new BigDecimal("29000")); t7.setJoinDate(LocalDate.now().minusMonths(2)); t7.setStatus("ACTIVE"); trainerRepository.save(t7);
        Trainer t8 = new Trainer(); t8.setFullName("Ananya Iyer"); t8.setEmail("ananya@fitdesk.com"); t8.setSpecialization("Nutrition & Dietetics"); t8.setSalary(new BigDecimal("34000")); t8.setJoinDate(LocalDate.now().minusMonths(1)); t8.setStatus("ACTIVE"); trainerRepository.save(t8);
        
        List<Trainer> trainers = List.of(t1, t2, t3, t4, t5, t6, t7, t8);

        // 4. Members
        String[] names = {"Arjun Kumar", "Neha Gupta", "Vikram Singh", "Pooja Sharma", "Karan Desai", "Anjali Verma", "Rohan Mehta", "Divya Reddy", "Sanjay Joshi", "Kavita Rao"};
        List<Member> members = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            Member m = new Member();
            m.setFullName(names[i]);
            m.setEmail("member" + i + "@example.com");
            m.setPhone("987654321" + i);
            m.setGender(i % 2 == 0 ? "Male" : "Female");
            m.setDateOfBirth(LocalDate.of(1990 + random.nextInt(10), random.nextInt(12) + 1, random.nextInt(28) + 1));
            m.setJoinDate(LocalDate.now().minusMonths(random.nextInt(6)));
            m.setStatus(i < 8 ? "ACTIVE" : "INACTIVE");
            m.setPlan(plans.get(random.nextInt(plans.size())));
            m.setTrainer(trainers.get(random.nextInt(trainers.size())));
            if (m.getPlan() != null) {
                m.setPlanStartDate(m.getJoinDate());
                m.setPlanEndDate(m.getJoinDate().plusMonths(m.getPlan().getDurationMonths()));
            }
            members.add(memberRepository.save(m));
        }

        // 5. Payments
        String[] methods = {"CASH", "CARD", "UPI", "ONLINE"};
        for (int i = 0; i < 25; i++) {
            Payment p = new Payment();
            Member m = members.get(random.nextInt(members.size()));
            p.setMember(m);
            p.setAmount(m.getPlan().getPrice());
            p.setPaymentMethod(methods[random.nextInt(methods.length)]);
            p.setStatus("COMPLETED");
            p.setPaymentDate(LocalDate.now().minusMonths(random.nextInt(6)).minusDays(random.nextInt(28)));
            paymentRepository.save(p);
        }

        // 6. Attendance
        for (int i = 0; i < 35; i++) {
            Attendance a = new Attendance();
            Member m = members.get(random.nextInt(members.size()));
            a.setMember(m);
            LocalDateTime checkIn = LocalDateTime.now().minusDays(random.nextInt(14)).minusHours(random.nextInt(10) + 1);
            a.setCheckIn(checkIn);
            if (random.nextInt(10) > 1) { // mostly checked out
                a.setCheckOut(checkIn.plusHours(1).plusMinutes(random.nextInt(60)));
            }
            attendanceRepository.save(a);
        }
    }
}
