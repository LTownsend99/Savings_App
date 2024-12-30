package com.example.savings_app.repository;

import com.example.savings_app.model.Savings;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsRepository extends JpaRepository<Savings, Integer> {

  List<Savings> findByDate(LocalDate date);

  Optional<Savings> findByMilestoneId(int milestoneId);
}
