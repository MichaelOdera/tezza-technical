-- Notification Service schema reference.
-- Hibernate owns demo database creation (spring.jpa.hibernate.ddl-auto=update).
CREATE TABLE IF NOT EXISTS notification (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    loan_id UUID NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
