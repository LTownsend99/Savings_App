package com.example.savings_app.repository;

import com.example.savings_app.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MilestoneRepository extends JpaRepository<Milestone, Integer> {

    Optional<Milestone>findByName(String name);

    List<Milestone> findByStartDate(Date startDate);

    List<Milestone> findByCompletionDate(Date completionDate);

    List<Milestone> findByStatus(Enum status);

    void deleteById(int milestoneId);

}
