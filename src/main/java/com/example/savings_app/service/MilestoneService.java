package com.example.savings_app.service;

import com.example.savings_app.model.Milestone;
import com.example.savings_app.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;

    @Autowired
    public MilestoneService(MilestoneRepository milestoneRepository) {
        this.milestoneRepository = milestoneRepository;
    }

    public Optional<Milestone> getMilestoneByMilestoneId(int milestoneId) {

        try {
            return milestoneRepository.findById(milestoneId);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided ID is invalid
            throw new IllegalArgumentException("Invalid Milestone milestoneId: " + milestoneId, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve Milestone with milestoneId: " + milestoneId, e);
        }
    }

    public Optional<Milestone> findByName(String name) {
        try {
            return milestoneRepository.findByName(name);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided name is invalid
            throw new IllegalArgumentException("Invalid milestone name: " + name, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve Milestone with name: " + name, e);
        }
    }

    public List<Milestone> findByStartDate(Date startDate) {
        try {
            return milestoneRepository.findByStartDate(startDate);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided start date is invalid
            throw new IllegalArgumentException("Invalid start date: " + startDate, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve Milestones with start date: " + startDate, e);
        }
    }

    public List<Milestone> findByCompletionDate(Date completionDate) {
        try {
            return milestoneRepository.findByCompletionDate(completionDate);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided completion date is invalid
            throw new IllegalArgumentException("Invalid completion date: " + completionDate, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve Milestones with completion date: " + completionDate, e);
        }
    }

    public List<Milestone> findByStatus(Enum status) {
        try {
            return milestoneRepository.findByStatus(status);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided status is invalid
            throw new IllegalArgumentException("Invalid status: " + status, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve Milestones with status: " + status, e);
        }
    }

}
