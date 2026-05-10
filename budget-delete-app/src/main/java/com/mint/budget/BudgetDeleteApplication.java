package com.mint.budget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mint.budget")
public class BudgetDeleteApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetDeleteApplication.class, args);
    }
}
