-- Product Service schema reference.
-- Hibernate owns demo database creation (spring.jpa.hibernate.ddl-auto=update).
CREATE TABLE IF NOT EXISTS product (
    id UUID PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    tenure_value INTEGER NOT NULL,
    tenure_unit VARCHAR(20) NOT NULL,
    service_fee_type VARCHAR(20) NOT NULL,
    service_fee_amount DECIMAL(19, 2) NOT NULL,
    daily_fee DECIMAL(19, 2) NOT NULL,
    late_fee_amount DECIMAL(19, 2) NOT NULL,
    late_fee_trigger_days INTEGER NOT NULL,
    active BOOLEAN NOT NULL
);
