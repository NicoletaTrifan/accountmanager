package com.example.accountmanager.service;

import com.example.accountmanager.exception.InsufficientBalanceException;
import com.example.accountmanager.model.CustomerAccount;
import com.example.accountmanager.repository.AccountHistoryRepository;
import com.example.accountmanager.repository.AccountManagerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountManagerServiceTest {
    @Mock
    private AccountManagerRepository accountManagerRepository;

    @Mock
    private AccountHistoryRepository accountHistoryRepository;

    @InjectMocks
    private AccountManagerService accountManagerService;

    @Test
    public void createCustomerTest() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000.0));

        CustomerAccount savedCustomerAccount = mock(CustomerAccount.class);
        when(savedCustomerAccount.getId()).thenReturn(1L);

        when(accountManagerRepository.existsByEmail(customerAccount.getEmail())).thenReturn(false);
        when(accountManagerRepository.save(customerAccount)).thenReturn(savedCustomerAccount);

        accountManagerService.createCustomer(customerAccount);

        verify(accountManagerRepository).existsByEmail("alice3@example.com");
        verify(accountManagerRepository).save(customerAccount);
    }

    @Test
    public void getAllCustomersTest() {
        CustomerAccount customerAccount1 = new CustomerAccount();
        customerAccount1.setName("Alice");
        customerAccount1.setEmail("alice3@example.com");
        customerAccount1.setAccountBalance(BigDecimal.valueOf(1000.0));

        CustomerAccount customerAccount2 = new CustomerAccount();
        customerAccount2.setName("John");
        customerAccount2.setEmail("john2@example.com");
        customerAccount2.setAccountBalance(BigDecimal.valueOf(500.0));

        when(accountManagerRepository.findAll()).thenReturn(List.of(customerAccount1, customerAccount2));

        List<CustomerAccount> accountList = accountManagerService.getAllCustomers();

        verify(accountManagerRepository).findAll();
        assertEquals(2, accountList.size());
    }

    @Test
    public void depositMoneyForCustomerTest() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("John");
        customerAccount.setEmail("john2@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(500.0));

        when(accountManagerRepository.findById(anyLong())).thenReturn(Optional.of(customerAccount));

        accountManagerService.depositMoneyForCustomer(1L, BigDecimal.valueOf(75));

        verify(accountManagerRepository).findById(1L);
        verify(accountManagerRepository).save(customerAccount);
        assertEquals(BigDecimal.valueOf(575.0), customerAccount.getAccountBalance());
    }

    @Test
    public void withdrawMoneyForCustomerTest() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("John");
        customerAccount.setEmail("john2@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(500.0));

        when(accountManagerRepository.findById(anyLong())).thenReturn(Optional.of(customerAccount));

        accountManagerService.withdrawMoneyForCustomer(1L, BigDecimal.valueOf(75));

        verify(accountManagerRepository).findById(1L);
        verify(accountManagerRepository).save(customerAccount);
        assertEquals(BigDecimal.valueOf(425.0), customerAccount.getAccountBalance());
    }

    @Test
    public void withdrawMoneyForCustomer_insufficientBalanceException() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("John");
        customerAccount.setEmail("john2@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(500.0));

        when(accountManagerRepository.findById(anyLong())).thenReturn(Optional.of(customerAccount));

        assertThrows(InsufficientBalanceException.class,
                () -> accountManagerService.withdrawMoneyForCustomer(1L, BigDecimal.valueOf(700)));
    }

}
