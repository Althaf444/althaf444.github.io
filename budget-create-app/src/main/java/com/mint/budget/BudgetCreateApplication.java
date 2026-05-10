package com.mint.budget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mint.budget")
public class BudgetCreateApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetCreateApplication.class, args);
    }
}
