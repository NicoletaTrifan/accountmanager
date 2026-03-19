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

/**
 * REST Controller responsible for managing account details, creation of a customer account and handling
 * withdrawal and deposit operations
 */
@RestController
@RequestMapping("/accounts")
public class AccountManagerController {

    private final AccountManagerService accountManagerService;

    public AccountManagerController(AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }

    /**
     * @param customerAccount the {@link CustomerAccount} object containing the data required to create a customer
     * @return a {@link ResponseEntity} with {@link HttpStatus} {@code 201 CREATED}
     * @throws EmailAlreadyExistsException if a customer with the same email already exists
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createCustomer(@RequestBody CustomerAccount customerAccount) {
        accountManagerService.createCustomer(customerAccount);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * @return a {@link ResponseEntity} with a list of {@link CustomerAccount} records
     */
    @GetMapping
    public ResponseEntity<List<CustomerAccount>> getAllCustomers() {
        return ResponseEntity.ok(accountManagerService.getAllCustomers());
    }

    /**
     * @param id the unique identifies for customer account
     * @return a {@link ResponseEntity} with a {@link CustomerAccount} object
     * @throws CustomerNotFoundException if a customer with given id is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerAccount> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(accountManagerService.getCustomerById(id));
    }

    /**
     * @param id     the unique identifier for the customer account
     * @param amount that is wanted to be deposited on the account
     * @return a {@link ResponseEntity} with {@link HttpStatus} {@code 200 OK}
     * @throws CustomerNotFoundException if a customer with given id is not found
     */
    @PatchMapping("/{id}/deposit")
    public ResponseEntity<Void> depositMoneyForCustomer(@PathVariable Long id, @RequestBody BigDecimal amount) {
        accountManagerService.depositMoneyForCustomer(id, amount);
        return ResponseEntity.ok().build();
    }

    /**
     * @param id     the unique identifier for the customer account
     * @param amount that is wanted to be withdrawn on the account
     * @return a {@link ResponseEntity} with {@link HttpStatus} {@code 200 OK}
     * @throws CustomerNotFoundException    if a customer with given id is not found
     * @throws InsufficientBalanceException if a customer tries to withdraw more than it is available
     */
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
