package com.example.accountmanager.service;

import com.example.accountmanager.model.AccountHistory;
import com.example.accountmanager.model.CustomerAccount;
import com.example.accountmanager.model.OperationType;
import com.example.accountmanager.repository.AccountHistoryRepository;
import com.example.accountmanager.repository.AccountManagerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountHistoryServiceTest {

    @Mock
    private AccountHistoryRepository accountHistoryRepository;

    @Mock
    private AccountManagerRepository accountManagerRepository;

    @InjectMocks
    private AccountHistoryService accountHistoryService;

    @Test
    public void getAccountHistoryTest() {
        CustomerAccount customerAccount1 = new CustomerAccount();
        customerAccount1.setName("Alice");
        customerAccount1.setEmail("alice3@example.com");
        customerAccount1.setAccountBalance(BigDecimal.valueOf(1000.0));

        AccountHistory accountHistoryOperation1 = new AccountHistory(1L, OperationType.DEPOSIT, BigDecimal.valueOf(50.0), BigDecimal.valueOf(1050.0), LocalDateTime.now());
        AccountHistory accountHistoryOperation2 = new AccountHistory(1L, OperationType.DEPOSIT, BigDecimal.valueOf(50.0), BigDecimal.valueOf(1100.0), LocalDateTime.now());

        when(accountManagerRepository.findById(1L)).thenReturn(Optional.of(customerAccount1));
        when(accountHistoryRepository.findByAccountId(1L)).thenReturn(Optional.of(List.of(accountHistoryOperation1, accountHistoryOperation2)));

        List<AccountHistory> accountHistory = accountHistoryService.getAccountHistory(1L);

        verify(accountManagerRepository).findById(1L);
        verify(accountHistoryRepository).findByAccountId(1L);
        assertEquals(2, accountHistory.size());

    }
}
