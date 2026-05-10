package com.mint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Scans {@code com.mint} so JPA picks up {@link com.mint.budget.Budget} and {@link com.mint.transaction.Transaction}.
 */
@SpringBootApplication(scanBasePackages = "com.mint")
public class BudgetStatusApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetStatusApplication.class, args);
    }
}
