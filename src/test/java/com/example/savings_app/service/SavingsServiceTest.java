package com.example.savings_app.service;

import com.example.savings_app.model.Milestone;
import com.example.savings_app.model.Savings;
import com.example.savings_app.repository.SavingsRepository;
import com.example.savings_app.service.SavingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SavingsServiceTest {

    private SavingsRepository savingsRepository;
    private SavingsService savingsService;

    private Savings savings;
    private Date savingsDate;

    @BeforeEach
    public void setUp() {
        savingsRepository = mock(SavingsRepository.class);
        savingsService = new SavingsService(savingsRepository);

        // Initialize test data
        savingsDate = new Date();
        savings = Savings.builder()
                .savingsId(1)
                .amount(BigDecimal.valueOf(150.00))
                .date(savingsDate)
                .milestone(Milestone.builder().milestoneId(1).build())
                .build();
    }

    @Test
    public void testFindById_Success() {
        when(savingsRepository.findById(1)).thenReturn(Optional.of(savings));

        Optional<Savings> result = savingsService.findById(1);

        assertTrue(result.isPresent(), "Savings should be found");
        assertEquals(savings.getSavingsId(), result.get().getSavingsId());
        verify(savingsRepository, times(1)).findById(1);
    }

    @Test
    public void testFindById_NotFound() {
        when(savingsRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Savings> result = savingsService.findById(99);

        assertFalse(result.isPresent(), "Savings should not be found");
        verify(savingsRepository, times(1)).findById(99);
    }

    @Test
    public void testFindByDate_Success() {
        when(savingsRepository.findByDate(savingsDate)).thenReturn(Arrays.asList(savings));

        List<Savings> result = savingsService.findByDate(savingsDate);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one Savings entry");
        assertEquals(savings.getSavingsId(), result.get(0).getSavingsId());
        verify(savingsRepository, times(1)).findByDate(savingsDate);
    }

    @Test
    public void testFindByDate_NoResults() {
        when(savingsRepository.findByDate(savingsDate)).thenReturn(Arrays.asList());

        List<Savings> result = savingsService.findByDate(savingsDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty");
        verify(savingsRepository, times(1)).findByDate(savingsDate);
    }

    @Test
    public void testFindByMilestoneId_Success() {
        when(savingsRepository.findByMilestoneId(1)).thenReturn(Optional.of(savings));

        Optional<Savings> result = savingsService.findByMilestoneId(1);

        assertTrue(result.isPresent(), "Savings should be found");
        assertEquals(savings.getMilestone().getMilestoneId(), result.get().getMilestone().getMilestoneId());
        verify(savingsRepository, times(1)).findByMilestoneId(1);
    }

    @Test
    public void testFindByMilestoneId_NotFound() {
        when(savingsRepository.findByMilestoneId(99)).thenReturn(Optional.empty());

        Optional<Savings> result = savingsService.findByMilestoneId(99);

        assertFalse(result.isPresent(), "Savings should not be found");
        verify(savingsRepository, times(1)).findByMilestoneId(99);
    }
}
