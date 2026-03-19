package com.example.accountmanager.controller;

import com.example.accountmanager.model.AccountHistory;
import com.example.accountmanager.model.CustomerAccount;
import com.example.accountmanager.repository.AccountHistoryRepository;
import com.example.accountmanager.repository.AccountManagerRepository;
import com.example.accountmanager.service.AccountManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountHistoryControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AccountManagerRepository accountManagerRepository;

    @Autowired
    private AccountHistoryRepository accountHistoryRepository;

    @Autowired
    private AccountManagerService accountManagerService;

    private final String PATH = "/accounts/";

    @BeforeEach
    void setup() {
        // Clean up the database before running a new test
        accountHistoryRepository.deleteAll();
        accountManagerRepository.deleteAll();
    }

    @Test
    public void getAccountHistoryTest() {
        // create customer
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));
        CustomerAccount savedCustomerAccount = accountManagerRepository.save(customerAccount);

        // perform a few operations on the account
        accountManagerService.depositMoneyForCustomer(savedCustomerAccount.getId(), BigDecimal.valueOf(50));
        accountManagerService.withdrawMoneyForCustomer(savedCustomerAccount.getId(), BigDecimal.valueOf(150));

        String url = PATH + savedCustomerAccount.getId() + "/history";
        ResponseEntity<AccountHistory[]> response = testRestTemplate.getForEntity(url, AccountHistory[].class);

        List<AccountHistory> accountHistoryList = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, accountHistoryList.size());
    }

    @Test
    public void getAccountHistory_customerNotFoundExceptionTest() {
        String url = PATH + "15/history";

        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
