package com.example.savings_app.service;

import com.example.savings_app.model.Savings;
import com.example.savings_app.repository.SavingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SavingsService {

    private final SavingsRepository savingsRepository;

    @Autowired
    public SavingsService(SavingsRepository savingsRepository) {
        this.savingsRepository = savingsRepository;
    }

    public Optional<Savings> getSavingsById(int savingsId) {
        try {
            return savingsRepository.findById(savingsId);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided ID is invalid
            throw new IllegalArgumentException("Invalid Savings ID: " + savingsId, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve Savings with ID: " + savingsId, e);
        }
    }

    public List<Savings> getSavingsByDate(Date date) {
        try {
            return savingsRepository.findByDate(date);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided date is invalid
            throw new IllegalArgumentException("Invalid Savings date: " + date, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve Savings with date: " + date, e);
        }
    }

    public Optional<Savings> getSavingsByMilestoneId(int milestoneId) {
        try {
            return savingsRepository.findByMilestoneId(milestoneId);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided Milestone ID is invalid
            throw new IllegalArgumentException("Invalid Milestone ID: " + milestoneId, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve Savings for Milestone ID: " + milestoneId, e);
        }
    }

}
