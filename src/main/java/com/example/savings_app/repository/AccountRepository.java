package com.example.savings_app.repository;

import com.example.savings_app.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {


    @Override
    Optional<Account> findById(Integer userId);

    Optional<Account> findByEmail(String email);
}
