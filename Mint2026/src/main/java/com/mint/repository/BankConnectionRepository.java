package com.mint.repository;

import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.User;
import com.mint.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankConnectionRepository extends JpaRepository<BankConnection, Long> {
    
    /**
     * Find all bank connections for a specific user
     */
    List<BankConnection> findByUser(User user);
    
    /**
     * Find a specific bank connection for a user and bank
     */
    Optional<BankConnection> findByUserAndBank(User user, Bank bank);
    
    /**
     * Find all bank connections with a specific status
     */
    List<BankConnection> findByStatus(ConnectionStatus status);
    
    /**
     * Find all connections for a user with a specific status
     */
    List<BankConnection> findByUserAndStatus(User user, ConnectionStatus status);
    
    /**
     * Find connections that need to be synced (status is LINKED and not recently synced)
     */
    List<BankConnection> findByStatusAndLastSyncDateBefore(ConnectionStatus status, LocalDateTime date);
    
    /**
     * Find all failed connections that need retry
     */
    List<BankConnection> findByStatusAndSyncFailureCountGreaterThan(ConnectionStatus status, Integer count);
}

