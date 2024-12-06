package com.example.savings_app.repository;

import com.example.savings_app.model.Savings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SavingsRepository extends JpaRepository<Savings, Integer> {

    Optional<Savings> findById(int savingsId);

    List<Savings> findByDate(Date date);

    Optional<Savings> findByMilestoneId(int milestoneId);

    void deleteById(int savingsId);

}
