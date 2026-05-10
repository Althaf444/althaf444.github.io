package com.mint.service.bank.api;

import com.mint.dto.BankAccountDto;
import com.mint.dto.BankTransactionDto;
import com.mint.exception.ConnectionTimeoutException;
import com.mint.exception.RateLimitException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for bank API client abstraction
 * Allows different implementations for real bank APIs and mocks
 */
public interface BankApiClient {
    
    /**
     * Authenticate and get access token for the bank API
     * @param credentials Bank credentials
     * @return Access token
     * @throws ConnectionTimeoutException if connection times out
     * @throws RateLimitException if rate limit is exceeded
     */
    String authenticate(String credentials) throws ConnectionTimeoutException, RateLimitException;
    
    /**
     * Fetch account information from the bank
     * @param accountId Bank account ID
     * @param accessToken Authentication token
     * @return Bank account details
     * @throws ConnectionTimeoutException if connection times out
     * @throws RateLimitException if rate limit is exceeded
     */
    BankAccountDto fetchAccount(String accountId, String accessToken) throws ConnectionTimeoutException, RateLimitException;
    
    /**
     * Fetch transactions for a specific account
     * @param accountId Bank account ID
     * @param accessToken Authentication token
     * @param from Start date for transaction query
     * @param to End date for transaction query
     * @return List of transactions
     * @throws ConnectionTimeoutException if connection times out
     * @throws RateLimitException if rate limit is exceeded
     */
    List<BankTransactionDto> fetchTransactions(String accountId, String accessToken, LocalDateTime from, LocalDateTime to) 
            throws ConnectionTimeoutException, RateLimitException;
    
    /**
     * Validate the connection to the bank API
     * @return true if connection is valid, false otherwise
     */
    boolean validateConnection();
}

