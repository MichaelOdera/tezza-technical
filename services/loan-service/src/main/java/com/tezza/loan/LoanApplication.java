package com.tezza.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.tezza.loan.config.LoanServiceProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(LoanServiceProperties.class)
public class LoanApplication {
    public static void main(String[] args) { SpringApplication.run(LoanApplication.class, args); }
}