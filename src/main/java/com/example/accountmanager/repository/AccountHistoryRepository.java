package com.example.accountmanager.repository;

import com.example.accountmanager.model.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {

    Optional<List<AccountHistory>> findByAccountId(Long accountId);
}
