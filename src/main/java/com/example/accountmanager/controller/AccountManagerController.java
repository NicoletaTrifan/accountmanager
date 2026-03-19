package com.example.accountmanager.controller;

import com.example.accountmanager.exception.CustomerNotFoundException;
import com.example.accountmanager.exception.InsufficientBalanceException;
import com.example.accountmanager.exception.EmailAlreadyExistsException;
import com.example.accountmanager.model.CustomerAccount;
import com.example.accountmanager.service.AccountManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountManagerController {

    private final AccountManagerService accountManagerService;

    public AccountManagerController(AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createCustomer(@RequestBody CustomerAccount customerAccount) {
        accountManagerService.createCustomer(customerAccount);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerAccount>> getAllCustomers() {
        return ResponseEntity.ok(accountManagerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerAccount> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(accountManagerService.getCustomerById(id));
    }

    @PatchMapping("/{id}/deposit")
    public ResponseEntity<Void> depositMoneyForCustomer(@PathVariable Long id, @RequestBody BigDecimal amount) {
        accountManagerService.depositMoneyForCustomer(id, amount);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdrawMoneyForCustomer(@PathVariable Long id, @RequestBody BigDecimal amount) {
        accountManagerService.withdrawMoneyForCustomer(id, amount);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
