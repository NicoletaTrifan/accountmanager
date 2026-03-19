package com.example.accountmanager.service;

import com.example.accountmanager.exception.CustomerNotFoundException;
import com.example.accountmanager.exception.InsufficientBalanceException;
import com.example.accountmanager.exception.EmailAlreadyExistsException;
import com.example.accountmanager.model.AccountHistory;
import com.example.accountmanager.model.CustomerAccount;
import com.example.accountmanager.model.OperationType;
import com.example.accountmanager.repository.AccountHistoryRepository;
import com.example.accountmanager.repository.AccountManagerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for retrieving account details and performing operations on the customer account.
 * Ensures that:
 * <ul>
 *     <li>Customer account can be created</li>
 *     <li>No two customers with the same email address can exist</li>
 *     <li>We can retrieve customer account details</li>
 *     <li>Account balance does not go below 0</li>
 *     <li>All operations are stored in history</li>
 * </ul>
 */
@Service
public class AccountManagerService {

    private final AccountManagerRepository accountManagerRepository;
    private final AccountHistoryRepository accountHistoryRepository;

    public AccountManagerService(AccountManagerRepository accountManagerRepository, AccountHistoryRepository accountHistoryRepository) {
        this.accountManagerRepository = accountManagerRepository;
        this.accountHistoryRepository = accountHistoryRepository;
    }

    /**
     * @return a list of all existing {@link CustomerAccount}
     */
    public List<CustomerAccount> getAllCustomers() {
        return accountManagerRepository.findAll();
    }

    /**
     * Created a new customer account
     *
     * @param customerAccount the customer account to be created
     * @throws EmailAlreadyExistsException if a customer with the same email address already exists
     */
    @Transactional
    public void createCustomer(CustomerAccount customerAccount) {
        if (!accountManagerRepository.existsByEmail(customerAccount.getEmail())) {
            CustomerAccount savedCustomerAccount = accountManagerRepository.save(customerAccount);
            // store first deposit on the account
            if (customerAccount.getAccountBalance().compareTo(BigDecimal.ZERO) > 0) {
                AccountHistory firstDepositOperation = new AccountHistory(
                        savedCustomerAccount.getId(),
                        OperationType.DEPOSIT,
                        customerAccount.getAccountBalance(),
                        customerAccount.getAccountBalance(),
                        LocalDateTime.now()
                );
                accountHistoryRepository.save(firstDepositOperation);
            }
        } else {
            throw new EmailAlreadyExistsException();
        }
    }

    /**
     * Retrieved details about a specific customer account
     *
     * @param id unique identifier for customer account
     * @return a {@link CustomerAccount}
     * @throws CustomerNotFoundException if a customer with the given id does not exist
     */
    public CustomerAccount getCustomerById(Long id) {
        if (accountManagerRepository.findById(id).isEmpty()) {
            throw new CustomerNotFoundException();
        }
        return accountManagerRepository.findById(id).orElseThrow();
    }

    /**
     * @param id     unique identifier for customer account
     * @param amount that is wanted to be deposited on the account
     * @throws CustomerNotFoundException if a customer with the given id does not exist
     */
    @Transactional
    public void depositMoneyForCustomer(Long id, BigDecimal amount) {
        CustomerAccount customerAccount = accountManagerRepository.findById(id).orElseThrow(CustomerNotFoundException::new);

        customerAccount.setAccountBalance(
                customerAccount.getAccountBalance().add(amount)
        );
        AccountHistory accountHistory = new AccountHistory(id, OperationType.DEPOSIT, amount, customerAccount.getAccountBalance(), LocalDateTime.now());

        accountManagerRepository.save(customerAccount);
        accountHistoryRepository.save(accountHistory);
    }

    /**
     * @param id     unique identifier for customer account
     * @param amount that is wanted to be withdrawn on the account
     * @throws InsufficientBalanceException if it is intended to go below 0 balance
     */
    @Transactional
    public void withdrawMoneyForCustomer(Long id, BigDecimal amount) {
        CustomerAccount customerAccount = accountManagerRepository.findById(id).orElseThrow(CustomerNotFoundException::new);

        BigDecimal newBalance = customerAccount.getAccountBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException();
        }

        customerAccount.setAccountBalance(newBalance);
        AccountHistory accountHistory = new AccountHistory(id, OperationType.WITHDRAW, amount, customerAccount.getAccountBalance(), LocalDateTime.now());

        accountManagerRepository.save(customerAccount);
        accountHistoryRepository.save(accountHistory);

    }
}
