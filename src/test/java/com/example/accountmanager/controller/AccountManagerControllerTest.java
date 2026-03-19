package com.example.accountmanager.controller;

import com.example.accountmanager.exception.CustomerNotFoundException;
import com.example.accountmanager.exception.EmailAlreadyExistsException;
import com.example.accountmanager.exception.InsufficientBalanceException;
import com.example.accountmanager.model.CustomerAccount;
import com.example.accountmanager.repository.AccountHistoryRepository;
import com.example.accountmanager.repository.AccountManagerRepository;
import com.example.accountmanager.service.AccountManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;


import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountManagerControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AccountManagerRepository accountManagerRepository;

    @Autowired
    private AccountHistoryRepository accountHistoryRepository;

    @Autowired
    private AccountManagerService accountManagerService;

    private final String PATH = "/accounts";

    @BeforeEach
    void setup() {
        // Clean up the database before running a new test
        accountHistoryRepository.deleteAll();
        accountManagerRepository.deleteAll();
    }

    @Test
    public void createCustomerTest() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));
        String url = PATH + "/create";

        ResponseEntity<Void> responseEntity = testRestTemplate.postForEntity(url, customerAccount, Void.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertTrue(accountManagerRepository.existsByEmail("alice3@example.com"));
    }

    @Test
    public void createCustomer_emailAlreadyExistsExceptionTest() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));
        accountManagerService.createCustomer(customerAccount);

        CustomerAccount customerAccount2 = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));

        String url = PATH + "/create";

        ResponseEntity<String > responseEntity = testRestTemplate.postForEntity(url, customerAccount, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(EmailAlreadyExistsException.MESSAGE, responseEntity.getBody());
    }

    @Test
    public void getAllCustomersTest() {
        createCustomers();

        ResponseEntity<CustomerAccount[]> responseEntity = testRestTemplate.getForEntity(PATH, CustomerAccount[].class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(3, Arrays.asList(responseEntity.getBody()).size());
    }

    @Test
    public void getCustomerById() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));

        CustomerAccount savedCustomerAccount = accountManagerRepository.save(customerAccount);
        String url = PATH + "/" + savedCustomerAccount.getId();

        ResponseEntity<CustomerAccount> responseEntity = testRestTemplate.getForEntity(url, CustomerAccount.class);
        CustomerAccount responseCustomerAccount = responseEntity.getBody();
        assertEquals("Alice", responseCustomerAccount.getName());
        assertEquals("alice3@example.com", responseCustomerAccount.getEmail());
        assertEquals(new BigDecimal("1000.00"), responseCustomerAccount.getAccountBalance());

    }

    @Test
    public void getCustomerById_CustomerNotFoundExceptionTest() {
        String url = PATH + "/15";

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(CustomerNotFoundException.MESSAGE, responseEntity.getBody());
    }

    @Test
    public void depositMoneyForCustomerTest() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));

        CustomerAccount savedCustomerAccount = accountManagerRepository.save(customerAccount);
        String url = PATH + "/" + savedCustomerAccount.getId() + "/deposit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BigDecimal> httpEntity = new HttpEntity<>(BigDecimal.valueOf(50), headers);

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                url,
                HttpMethod.PATCH,
                httpEntity,
                Void.class);

        CustomerAccount updatedCustomer = accountManagerRepository.findById(savedCustomerAccount.getId()).get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(new BigDecimal("1050.00"), updatedCustomer.getAccountBalance());
    }

    @Test
    public void withdrawMoneyForCustomerTest() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));

        CustomerAccount savedCustomerAccount = accountManagerRepository.save(customerAccount);
        String url = PATH + "/" + savedCustomerAccount.getId() + "/withdraw";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BigDecimal> httpEntity = new HttpEntity<>(BigDecimal.valueOf(100), headers);

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                url,
                HttpMethod.PATCH,
                httpEntity,
                Void.class);

        CustomerAccount updatedCustomer = accountManagerRepository.findById(savedCustomerAccount.getId()).get();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(new BigDecimal("900.00"), updatedCustomer.getAccountBalance());
    }

    @Test
    public void withdrawMoneyForCustomer_InsufficientBalanceExceptionTest() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));

        CustomerAccount savedCustomerAccount = accountManagerRepository.save(customerAccount);
        String url = PATH + "/" + savedCustomerAccount.getId() + "/withdraw";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BigDecimal> httpEntity = new HttpEntity<>(BigDecimal.valueOf(3000), headers);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                url,
                HttpMethod.PATCH,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(InsufficientBalanceException.MESSAGE, responseEntity.getBody());
    }

    public void createCustomers() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setName("Alice");
        customerAccount.setEmail("alice3@example.com");
        customerAccount.setAccountBalance(BigDecimal.valueOf(1000));

        CustomerAccount customerAccount2 = new CustomerAccount();
        customerAccount2.setName("John");
        customerAccount2.setEmail("john3@example.com");
        customerAccount2.setAccountBalance(BigDecimal.valueOf(600));

        CustomerAccount customerAccount3 = new CustomerAccount();
        customerAccount3.setName("Mary J");
        customerAccount3.setEmail("maryj@example.com");
        customerAccount3.setAccountBalance(BigDecimal.valueOf(400));

        CustomerAccount savedCustomerAccount = accountManagerRepository.save(customerAccount);
        CustomerAccount savedCustomerAccount2 = accountManagerRepository.save(customerAccount2);
        CustomerAccount savedCustomerAccount3 = accountManagerRepository.save(customerAccount3);

    }

}
