package com.example.accountmanager.service;

import com.example.accountmanager.exception.CustomerNotFoundException;
import com.example.accountmanager.model.AccountHistory;
import com.example.accountmanager.repository.AccountHistoryRepository;
import com.example.accountmanager.repository.AccountManagerRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AccountHistoryService {

    private final AccountHistoryRepository accountHistoryRepository;
    private final AccountManagerRepository accountManagerRepository;

    public AccountHistoryService(AccountHistoryRepository accountHistoryRepository, AccountManagerRepository accountManagerRepository) {
        this.accountHistoryRepository = accountHistoryRepository;
        this.accountManagerRepository = accountManagerRepository;
    }

    public List<AccountHistory> getAccountHistory(Long accountId) {
        accountManagerRepository.findById(accountId).orElseThrow(CustomerNotFoundException::new);
        return accountHistoryRepository.findByAccountId(accountId).orElseGet(Collections::emptyList);
    }
}
