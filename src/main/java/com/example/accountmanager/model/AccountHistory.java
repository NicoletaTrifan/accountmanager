package com.example.accountmanager.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a record of one operation (deposit/withdrawal) performed to the account. Stores
 * information about when was the operation done, what kind of operation, amount and the amount left on the account
 * after it was done.
 */
@Entity
public class AccountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    private BigDecimal amount;

    @Column(name = "balance_after")
    private BigDecimal balanceAfterOperation;

    private LocalDateTime createdAt;

    public AccountHistory() {
    }

    public AccountHistory(Long accountId,
                          OperationType operationType,
                          BigDecimal amount,
                          BigDecimal balanceAfterOperation,
                          LocalDateTime createdAt) {
        this.accountId = accountId;
        this.operationType = operationType;
        this.amount = amount;
        this.balanceAfterOperation = balanceAfterOperation;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfterOperation() {
        return balanceAfterOperation;
    }

    public void setBalanceAfterOperation(BigDecimal balanceAfterOperation) {
        this.balanceAfterOperation = balanceAfterOperation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
