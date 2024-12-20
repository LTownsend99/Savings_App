package com.example.savings_app.repository;

import com.example.savings_app.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MilestoneRepository extends JpaRepository<Milestone, Integer> {

    Optional<Milestone>findByMilestoneName(String milestoneName);

    List<Milestone> findByStartDate(LocalDate startDate);

    List<Milestone> findByCompletionDate(LocalDate completionDate);

    List<Milestone> findByStatus(Enum status);

}
