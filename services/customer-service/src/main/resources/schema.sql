-- Customer Service schema reference.
-- Hibernate owns demo database creation (spring.jpa.hibernate.ddl-auto=update).
CREATE TABLE IF NOT EXISTS customer (
    id UUID PRIMARY KEY,
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    loan_limit DECIMAL(19, 2) NOT NULL,
    available_limit DECIMAL(19, 2) NOT NULL
);
