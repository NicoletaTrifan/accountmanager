package com.example.accountmanager.repository;

import com.example.accountmanager.model.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing {@link CustomerAccount} data
 */
public interface AccountManagerRepository extends JpaRepository<CustomerAccount, Long> {

    Optional<CustomerAccount> findById(Long id);

    boolean existsByEmail(String email);
}
