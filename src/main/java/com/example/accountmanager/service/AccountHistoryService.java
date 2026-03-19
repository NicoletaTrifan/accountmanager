package com.example.accountmanager.service;

import com.example.accountmanager.exception.CustomerNotFoundException;
import com.example.accountmanager.model.AccountHistory;
import com.example.accountmanager.repository.AccountHistoryRepository;
import com.example.accountmanager.repository.AccountManagerRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service responsible for retrieving account history records.
 * Ensures that account exists before fetching its associated data.
 */
@Service
public class AccountHistoryService {

    private final AccountHistoryRepository accountHistoryRepository;
    private final AccountManagerRepository accountManagerRepository;

    public AccountHistoryService(AccountHistoryRepository accountHistoryRepository, AccountManagerRepository accountManagerRepository) {
        this.accountHistoryRepository = accountHistoryRepository;
        this.accountManagerRepository = accountManagerRepository;
    }

    /**
     * @param accountId unique identifier for the customer account
     * @return a list of {@link AccountHistory} records, or an empty list if no operations were done
     * @throws CustomerNotFoundException if the account does not exist
     */
    public List<AccountHistory> getAccountHistory(Long accountId) {
        accountManagerRepository.findById(accountId).orElseThrow(CustomerNotFoundException::new);
        return accountHistoryRepository.findByAccountId(accountId).orElseGet(Collections::emptyList);
    }
}
