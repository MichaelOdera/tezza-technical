-- Loan Service schema reference.
-- Hibernate owns demo database creation (spring.jpa.hibernate.ddl-auto=update).
CREATE TABLE IF NOT EXISTS loan (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    idempotency_key VARCHAR(160) NOT NULL UNIQUE,
    product_id UUID NOT NULL,
    principal DECIMAL(19, 2) NOT NULL,
    outstanding_balance DECIMAL(19, 2) NOT NULL,
    structure VARCHAR(30) NOT NULL,
    state VARCHAR(30) NOT NULL,
    due_date DATE NOT NULL,
    consolidated_due_date DATE,
    fees_charged DECIMAL(19, 2) NOT NULL,
    late_fee_amount DECIMAL(19, 2) NOT NULL,
    late_fee_trigger_days INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS installment (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    installment_number INTEGER NOT NULL,
    amount_due DECIMAL(19, 2) NOT NULL,
    amount_paid DECIMAL(19, 2) NOT NULL,
    due_date DATE NOT NULL,
    state VARCHAR(30) NOT NULL
);
