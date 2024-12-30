package com.example.savings_app.repository;

import com.example.savings_app.model.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {

  Optional<Account> findByEmail(String email);
}
