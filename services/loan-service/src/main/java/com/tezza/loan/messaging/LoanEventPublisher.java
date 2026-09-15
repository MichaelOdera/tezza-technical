package com.tezza.loan.messaging;

import com.tezza.contracts.LoanCreatedEvent;
import com.tezza.contracts.LoanOverdueEvent;
import com.tezza.contracts.Messaging;
import com.tezza.contracts.RepaymentReceivedEvent;
import com.tezza.loan.model.Loan;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public LoanEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void loanCreated(Loan loan) {
        rabbitTemplate.convertAndSend(
                Messaging.EXCHANGE,
                Messaging.LOAN_CREATED,
                new LoanCreatedEvent(loan.getId(), loan.getCustomerId(), loan.getPrincipal(), Messaging.LOAN_CREATED));
    }

    public void repayment(Loan loan, BigDecimal amount) {
        rabbitTemplate.convertAndSend(
                Messaging.EXCHANGE,
                Messaging.REPAYMENT_RECEIVED,
                new RepaymentReceivedEvent(loan.getId(), loan.getCustomerId(), amount, Messaging.REPAYMENT_RECEIVED));
    }

    public void overdue(Loan loan) {
        rabbitTemplate.convertAndSend(
                Messaging.EXCHANGE,
                Messaging.LOAN_OVERDUE,
                new LoanOverdueEvent(loan.getId(), loan.getCustomerId(), Messaging.LOAN_OVERDUE));
    }
}
