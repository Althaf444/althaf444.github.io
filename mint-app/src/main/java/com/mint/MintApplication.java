package com.mint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mint")
public class MintApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintApplication.class, args);
    }

}
