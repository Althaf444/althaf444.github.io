package com.mint.repository;

import com.mint.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    /**
     * Find a bank by its name
     */
    Bank findByName(String name);
}

