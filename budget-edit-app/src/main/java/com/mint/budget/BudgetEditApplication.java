package com.mint.budget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mint.budget")
public class BudgetEditApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetEditApplication.class, args);
    }
}
