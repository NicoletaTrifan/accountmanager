package com.example.accountmanager.controller;

import com.example.accountmanager.exception.CustomerNotFoundException;
import com.example.accountmanager.model.AccountHistory;
import com.example.accountmanager.service.AccountHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for managing account history operations.
 */
@RestController
@RequestMapping("/accounts/{accountId}/history")
public class AccountHistoryController {

    private final AccountHistoryService accountHistoryService;

    public AccountHistoryController(AccountHistoryService accountHistoryService) {
        this.accountHistoryService = accountHistoryService;
    }

    /**
     * Retrieves the history of operations on a specific account
     * @param accountId the identifier of the account
     * @return a {@link ResponseEntity} with a list of {@link AccountHistory} records
     * @throws CustomerNotFoundException if a customer with given id is not found
     */
    @GetMapping
    public ResponseEntity<List<AccountHistory>> getAccountHistory(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountHistoryService.getAccountHistory(accountId));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
