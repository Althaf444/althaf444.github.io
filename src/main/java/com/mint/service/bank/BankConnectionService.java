package com.mint.service.bank;

import com.mint.entity.Bank;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.SyncStatus;
import com.mint.entity.User;
import com.mint.exception.BankConnectionException;
import com.mint.repository.BankConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing bank connections
 * Handles CRUD operations and connection state management
 */
@Service
@Transactional
public class BankConnectionService {
    private static final Logger logger = LoggerFactory.getLogger(BankConnectionService.class);
    
    private final BankConnectionRepository bankConnectionRepository;
    
    public BankConnectionService(BankConnectionRepository bankConnectionRepository) {
        this.bankConnectionRepository = bankConnectionRepository;
    }
    
    /**
     * Create a new bank connection
     * @param user User creating the connection
     * @param bank Bank to connect to
     * @param accountId Account ID from the bank
     * @return Created BankConnection
     * @throws BankConnectionException if connection already exists or if required fields are missing
     */
    public BankConnection createConnection(User user, Bank bank, String accountId) throws BankConnectionException {
        validateInputs(user, bank, accountId);
        
        // Check if connection already exists
        Optional<BankConnection> existing = bankConnectionRepository.findByUserAndBank(user, bank);
        if (existing.isPresent()) {
            throw new BankConnectionException("Bank connection already exists for this user and bank");
        }
        
        BankConnection connection = new BankConnection();
        connection.setUser(user);
        connection.setBank(bank);
        connection.setAccountId(accountId);
        connection.setStatus(ConnectionStatus.PENDING);
        connection.setSyncStatus(SyncStatus.IDLE);
        connection.setSyncFailureCount(0);
        connection.setCreatedAt(LocalDateTime.now());
        connection.setUpdatedAt(LocalDateTime.now());
        
        BankConnection saved = bankConnectionRepository.save(connection);
        logger.info("Created new bank connection: userId={}, bankId={}, accountId={}", user.getId(), bank.getId(), accountId);
        
        return saved;
    }
    
    /**
     * Update connection status
     * @param connectionId Connection ID
     * @param status New status
     * @return Updated BankConnection
     * @throws BankConnectionException if connection not found
     */
    public BankConnection updateConnectionStatus(Long connectionId, ConnectionStatus status) throws BankConnectionException {
        BankConnection connection = bankConnectionRepository.findById(connectionId)
            .orElseThrow(() -> new BankConnectionException("Bank connection not found with id: " + connectionId));
        
        connection.setStatus(status);
        connection.setUpdatedAt(LocalDateTime.now());
        
        BankConnection updated = bankConnectionRepository.save(connection);
        logger.info("Updated connection status: connectionId={}, status={}", connectionId, status);
        
        return updated;
    }
    
    /**
     * Update sync status and record sync attempt
     * @param connectionId Connection ID
     * @param syncStatus New sync status
     * @param errorMessage Error message (if sync failed)
     * @return Updated BankConnection
     * @throws BankConnectionException if connection not found
     */
    public BankConnection updateSyncStatus(Long connectionId, SyncStatus syncStatus, String errorMessage) throws BankConnectionException {
        BankConnection connection = bankConnectionRepository.findById(connectionId)
            .orElseThrow(() -> new BankConnectionException("Bank connection not found with id: " + connectionId));
        
        connection.setSyncStatus(syncStatus);
        connection.setLastSyncAttempt(LocalDateTime.now());
        connection.setUpdatedAt(LocalDateTime.now());
        
        // Update failure count based on status
        if (syncStatus == SyncStatus.SUCCESS) {
            connection.setLastSyncDate(LocalDateTime.now());
            connection.setSyncFailureCount(0);
            connection.setSyncErrorMessage(null);
            logger.info("Sync successful for connectionId={}", connectionId);
        } else if (syncStatus == SyncStatus.FAILED) {
            Integer failureCount = connection.getSyncFailureCount();
            connection.setSyncFailureCount((failureCount != null ? failureCount : 0) + 1);
            connection.setSyncErrorMessage(errorMessage);
            logger.warn("Sync failed for connectionId={}: {}", connectionId, errorMessage);
        }
        
        return bankConnectionRepository.save(connection);
    }
    
    /**
     * Get all connections for a user
     * @param user User entity
     * @return List of BankConnections
     */
    public List<BankConnection> getUserConnections(User user) {
        return bankConnectionRepository.findByUser(user);
    }
    
    /**
     * Get connection status for a user and bank
     * @param user User entity
     * @param bank Bank entity
     * @return Optional containing BankConnection if found
     */
    public Optional<BankConnection> getConnection(User user, Bank bank) {
        return bankConnectionRepository.findByUserAndBank(user, bank);
    }
    
    /**
     * Get connection by ID
     * @param connectionId Connection ID
     * @return Optional containing BankConnection if found
     */
    public Optional<BankConnection> getConnection(Long connectionId) {
        return bankConnectionRepository.findById(connectionId);
    }
    
    /**
     * Delete a bank connection
     * @param connectionId Connection ID
     * @throws BankConnectionException if connection not found
     */
    public void deleteConnection(Long connectionId) throws BankConnectionException {
        BankConnection connection = bankConnectionRepository.findById(connectionId)
            .orElseThrow(() -> new BankConnectionException("Bank connection not found with id: " + connectionId));
        
        bankConnectionRepository.delete(connection);
        logger.info("Deleted bank connection: connectionId={}", connectionId);
    }
    
    /**
     * Get all connections that need syncing (haven't been synced in a while)
     * @param hoursOld Connections not synced in this many hours
     * @return List of BankConnections that need syncing
     */
    public List<BankConnection> getConnectionsNeedingSync(int hoursOld) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hoursOld);
        return bankConnectionRepository.findByStatusAndLastSyncDateBefore(ConnectionStatus.LINKED, cutoffTime);
    }
    
    /**
     * Get all failed connections that may need retry
     * @return List of BankConnections with failed status
     */
    public List<BankConnection> getFailedConnections() {
        return bankConnectionRepository.findByStatus(ConnectionStatus.FAILED);
    }
    
    /**
     * Validate that a connection can be used
     * @param connectionId Connection ID
     * @return true if connection is in LINKED status
     * @throws BankConnectionException if connection not found
     */
    public boolean isConnectionValid(Long connectionId) throws BankConnectionException {
        BankConnection connection = bankConnectionRepository.findById(connectionId)
            .orElseThrow(() -> new BankConnectionException("Bank connection not found with id: " + connectionId));
        
        return connection.getStatus() == ConnectionStatus.LINKED;
    }
    
    /**
     * Validate input parameters for connection creation
     * @param user User entity
     * @param bank Bank entity
     * @param accountId Account ID
     * @throws BankConnectionException if any parameter is invalid
     */
    private void validateInputs(User user, Bank bank, String accountId) throws BankConnectionException {
        if (user == null || user.getId() == null) {
            throw new BankConnectionException("Invalid user");
        }
        if (bank == null || bank.getId() == null) {
            throw new BankConnectionException("Invalid bank");
        }
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new BankConnectionException("Account ID cannot be empty");
        }
    }
}

