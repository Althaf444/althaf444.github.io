package com.mint.service.bank;

import com.mint.dto.BankAccountDto;
import com.mint.dto.BankTransactionDto;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.SyncStatus;
import com.mint.exception.BankSyncException;
import com.mint.exception.ConnectionTimeoutException;
import com.mint.exception.RateLimitException;
import com.mint.service.bank.api.BankApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for synchronizing bank data
 * Handles the core logic of fetching and validating bank data
 */
@Service
@Transactional
public class BankSyncService {
    private static final Logger logger = LoggerFactory.getLogger(BankSyncService.class);
    
    private static final int MAX_SYNC_FAILURES = 5;
    private static final int AUTH_RETRY_DELAY_MS = 1000;
    private static final int MAX_AUTH_RETRIES = 3;
    
    private final BankApiClient bankApiClient;
    private final BankConnectionService bankConnectionService;
    
    public BankSyncService(BankApiClient bankApiClient, BankConnectionService bankConnectionService) {
        this.bankApiClient = bankApiClient;
        this.bankConnectionService = bankConnectionService;
    }
    
    /**
     * Synchronize data for a specific bank connection
     * This is the main orchestration method for sync operations
     * 
     * @param connection Bank connection to sync
     * @param credentials Bank API credentials
     * @return true if sync was successful, false otherwise
     */
    public boolean synchronizeConnection(BankConnection connection, String credentials) {
        if (connection == null || connection.getId() == null) {
            logger.error("Invalid connection: connection or connection id is null");
            return false;
        }

        Long connectionId = connection.getId();
        logger.info("Starting synchronization for connection: {}", connectionId);
        
        try {
            if (credentials == null || credentials.trim().isEmpty()) {
                throw new BankSyncException("Credentials are missing");
            }
            if (connection.getAccountId() == null || connection.getAccountId().trim().isEmpty()) {
                throw new BankSyncException("Account ID is missing on bank connection");
            }

            // Check if already syncing to prevent concurrent syncs
            if (connection.getSyncStatus() == SyncStatus.SYNCING) {
                throw new BankSyncException("Synchronization already in progress for this connection");
            }
            
            // Update status to SYNCING
            bankConnectionService.updateSyncStatus(connectionId, SyncStatus.SYNCING, null);
            
            // Step 1: Authenticate with bank API
            String accessToken = authenticateWithRetry(credentials);
            logger.debug("Authentication successful for connection: {}", connectionId);
            
            // Step 2: Fetch account information
            BankAccountDto account = bankApiClient.fetchAccount(connection.getAccountId(), accessToken);
            validateAccount(account);
            logger.debug("Account fetched for connection: {}", connectionId);
            
            // Step 3: Fetch transactions (last 30 days)
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(30);
            var transactions = bankApiClient.fetchTransactions(
                connection.getAccountId(), 
                accessToken, 
                startDate, 
                endDate
            );
            logger.debug("Fetched {} transactions for connection: {}", transactions.size(), connectionId);
            
            // Step 4: Validate transaction data
            validateTransactions(transactions);
            
            // Step 5: Mark sync as successful
            bankConnectionService.updateSyncStatus(connectionId, SyncStatus.SUCCESS, null);
            
            // Update connection status to LINKED if it was PENDING
            if (connection.getStatus() == ConnectionStatus.PENDING) {
                bankConnectionService.updateConnectionStatus(connectionId, ConnectionStatus.LINKED);
            }
            
            logger.info("Synchronization completed successfully for connection: {}", connectionId);
            return true;
            
        } catch (BankSyncException e) {
            logger.error("Sync error for connection {}: {}", connectionId, e.getMessage());
            bankConnectionService.updateSyncStatus(connectionId, SyncStatus.FAILED, e.getMessage());
            handleSyncFailure(connection);
            return false;
            
        } catch (ConnectionTimeoutException e) {
            logger.error("Connection timeout for connection {}: {}", connectionId, e.getMessage());
            bankConnectionService.updateSyncStatus(connectionId, SyncStatus.FAILED, "Connection timeout: " + e.getMessage());
            handleSyncFailure(connection);
            return false;
            
        } catch (RateLimitException e) {
            logger.warn("Rate limit exceeded for connection {}: {}", connectionId, e.getMessage());
            bankConnectionService.updateSyncStatus(connectionId, SyncStatus.FAILED, "Rate limit exceeded");
            return false;
            
        } catch (Exception e) {
            logger.error("Unexpected error during sync for connection {}", connectionId, e);
            bankConnectionService.updateSyncStatus(connectionId, SyncStatus.FAILED, "Unexpected error: " + e.getMessage());
            handleSyncFailure(connection);
            return false;
        }
    }
    
    /**
     * Authenticate with retry logic
     * @param credentials Bank credentials
     * @return Access token
     * @throws BankSyncException if authentication fails after retries
     */
    private String authenticateWithRetry(String credentials) throws BankSyncException {
        ConnectionTimeoutException lastException = null;
        
        for (int attempt = 1; attempt <= MAX_AUTH_RETRIES; attempt++) {
            try {
                logger.debug("Authentication attempt {} of {}", attempt, MAX_AUTH_RETRIES);
                return bankApiClient.authenticate(credentials);
                
            } catch (ConnectionTimeoutException e) {
                lastException = e;
                if (attempt < MAX_AUTH_RETRIES) {
                    try {
                        Thread.sleep(AUTH_RETRY_DELAY_MS * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new BankSyncException("Authentication interrupted", ie);
                    }
                }
            } catch (RateLimitException e) {
                throw new BankSyncException("Rate limit exceeded during authentication", e);
            }
        }
        
        throw new BankSyncException("Authentication failed after " + MAX_AUTH_RETRIES + " attempts", lastException);
    }
    
    /**
     * Validate fetched account data
     * @param account Account DTO to validate
     * @throws BankSyncException if account data is invalid
     */
    private void validateAccount(BankAccountDto account) throws BankSyncException {
        if (account == null) {
            throw new BankSyncException("Account data is null");
        }
        if (account.getAccountId() == null || account.getAccountId().trim().isEmpty()) {
            throw new BankSyncException("Account ID is missing");
        }
        if (account.getBalance() == null) {
            throw new BankSyncException("Account balance is missing");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0 && 
            !"CREDIT".equalsIgnoreCase(account.getAccountType())) {
            throw new BankSyncException("Invalid account balance: negative balance for non-credit account");
        }
    }
    
    /**
     * Validate fetched transaction data
     * @param transactions List of transaction DTOs to validate
     * @throws BankSyncException if transaction data is invalid
     */
    private void validateTransactions(java.util.List<BankTransactionDto> transactions) throws BankSyncException {
        if (transactions == null) {
            throw new BankSyncException("Transactions list is null");
        }
        
        for (BankTransactionDto transaction : transactions) {
            if (transaction.getTransactionId() == null || transaction.getTransactionId().trim().isEmpty()) {
                throw new BankSyncException("Transaction has missing ID");
            }
            if (transaction.getAmount() == null) {
                throw new BankSyncException("Transaction has missing amount");
            }
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new BankSyncException("Transaction has negative amount");
            }
            if (transaction.getTransactionDate() == null) {
                throw new BankSyncException("Transaction has missing date");
            }
            if (transaction.getStatus() == null || transaction.getStatus().trim().isEmpty()) {
                throw new BankSyncException("Transaction has missing status");
            }
        }
    }
    
    /**
     * Handle synchronization failure
     * Increments failure count and may disable connection if failures exceed threshold
     * @param connection Bank connection that failed to sync
     */
    private void handleSyncFailure(BankConnection connection) {
        int currentFailures = bankConnectionService.getConnection(connection.getId())
            .map(BankConnection::getSyncFailureCount)
            .orElse(connection.getSyncFailureCount() != null ? connection.getSyncFailureCount() : 0);
        
        if (currentFailures >= MAX_SYNC_FAILURES) {
            logger.warn("Max sync failures ({}) reached for connection: {}. Disabling connection.", 
                       MAX_SYNC_FAILURES, connection.getId());
            try {
                bankConnectionService.updateConnectionStatus(connection.getId(), ConnectionStatus.FAILED);
            } catch (Exception e) {
                logger.error("Failed to disable connection after max failures", e);
            }
        }
    }
    
    /**
     * Validate bank API connection
     * @return true if bank API is reachable
     */
    public boolean validateBankConnection() {
        logger.info("Validating bank API connection");
        return bankApiClient.validateConnection();
    }
}

