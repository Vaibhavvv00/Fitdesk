-- ===================================================================
-- FitDesk — Database Schema (Reference DDL)
-- Note: JPA/Hibernate auto-creates tables via ddl-auto=update.
--       This file is for manual setup & interview reference.
-- ===================================================================

CREATE DATABASE IF NOT EXISTS fitdesk_db;
USE fitdesk_db;

-- -------------------------------------------------------------------
-- Admin Users (for Spring Security authentication)
-- -------------------------------------------------------------------
CREATE TABLE admin_users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'ADMIN',
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Membership Plans
-- -------------------------------------------------------------------
CREATE TABLE plans (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100)   NOT NULL,
    duration_months  INT            NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,
    description      TEXT,
    is_active        BOOLEAN        DEFAULT TRUE,
    created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Trainers
-- -------------------------------------------------------------------
CREATE TABLE trainers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100)   NOT NULL,
    email           VARCHAR(100)   NOT NULL UNIQUE,
    phone           VARCHAR(15),
    specialization  VARCHAR(100),
    salary          DECIMAL(10, 2),
    join_date       DATE           NOT NULL,
    status          VARCHAR(20)    DEFAULT 'ACTIVE',
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Members  (FK → plans, trainers)
-- -------------------------------------------------------------------
CREATE TABLE members (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone           VARCHAR(15),
    gender          VARCHAR(10),
    date_of_birth   DATE,
    join_date       DATE         NOT NULL,
    plan_start_date DATE,
    plan_end_date   DATE,
    status          VARCHAR(20)  DEFAULT 'ACTIVE',
    photo_url       VARCHAR(255),
    plan_id         BIGINT,
    trainer_id      BIGINT,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_member_plan    FOREIGN KEY (plan_id)    REFERENCES plans(id)    ON DELETE SET NULL,
    CONSTRAINT fk_member_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Interview talking point: JOIN members ON plans.id = members.plan_id
-- to get each member's plan name & price in a single query.

-- -------------------------------------------------------------------
-- Payments  (FK → members)
-- -------------------------------------------------------------------
CREATE TABLE payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT         NOT NULL,
    amount          DECIMAL(10, 2) NOT NULL,
    payment_date    DATE           NOT NULL,
    payment_method  VARCHAR(20)    NOT NULL,   -- CASH, CARD, UPI, ONLINE
    status          VARCHAR(20)    DEFAULT 'COMPLETED',  -- COMPLETED, PENDING, FAILED
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Interview talking point: 
-- SELECT DATE_FORMAT(payment_date, '%Y-%m') AS month, SUM(amount) AS revenue
-- FROM payments WHERE status = 'COMPLETED'
-- GROUP BY month ORDER BY month DESC LIMIT 6;

-- -------------------------------------------------------------------
-- Attendance  (FK → members)
-- -------------------------------------------------------------------
CREATE TABLE attendance (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT   NOT NULL,
    check_in   DATETIME NOT NULL,
    check_out  DATETIME,

    CONSTRAINT fk_attendance_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Interview talking point:
-- SELECT COUNT(DISTINCT member_id) FROM attendance
-- WHERE DATE(check_in) = CURDATE();
-- → "Today's attendance" stat card on the dashboard.
