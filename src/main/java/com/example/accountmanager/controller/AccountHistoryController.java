package com.example.accountmanager.controller;

import com.example.accountmanager.exception.CustomerNotFoundException;
import com.example.accountmanager.model.AccountHistory;
import com.example.accountmanager.service.AccountHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/history")
public class AccountHistoryController {

    private final AccountHistoryService accountHistoryService;

    public AccountHistoryController(AccountHistoryService accountHistoryService) {
        this.accountHistoryService = accountHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<AccountHistory>> getAccountHistory(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountHistoryService.getAccountHistory(accountId));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
