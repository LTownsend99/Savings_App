package com.example.savings_app.service;

import com.example.savings_app.model.Milestone;
import com.example.savings_app.repository.MilestoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class MilestoneServiceTest {

    private MilestoneRepository milestoneRepository;

    private MilestoneService milestoneService;

    private Milestone milestone;
    private Date startDate;
    private Date completionDate;


    @BeforeEach
    public void setUp() {
        milestoneRepository = mock(MilestoneRepository.class);
        milestoneService = new MilestoneService(milestoneRepository);
        // Initialize test data
        startDate = new Date();
        completionDate = new Date();
        milestone = Milestone.builder()
                .milestoneId(1)
                .milestoneName("Milestone")
                .targetAmount(BigDecimal.valueOf(200.00))
                .savedAmount(BigDecimal.valueOf(50.00))
                .startDate(startDate)
                .completionDate(completionDate)
                .status(Milestone.Status.ACTIVE)
                .build();
    }

    @Test
    public void testGetCustomerByMilestoneId_Success() {
        when(milestoneRepository.findById(1)).thenReturn(Optional.of(milestone));

        Optional<Milestone> result = milestoneService.getMilestoneByMilestoneId(1);

        assertTrue(result.isPresent(), "Milestone should be found");
        assertEquals(milestone.getMilestoneId(), result.get().getMilestoneId());
    }

    @Test
    public void testGetCustomerByMilestoneId_NotFound() {
        when(milestoneRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Milestone> result = milestoneService.getMilestoneByMilestoneId(1);

        assertFalse(result.isPresent(), "Milestone should not be found");
    }

    @Test
    public void testGetMilestoneByName_Success() {
        when(milestoneRepository.findByName("Test Milestone")).thenReturn(Optional.of(milestone));

        Optional<Milestone> result = milestoneService.getMilestoneByName("Test Milestone");

        assertTrue(result.isPresent(), "Milestone should be found by name");
        assertEquals("Milestone", result.get().getMilestoneName());
    }

    @Test
    public void testGetMilestoneByName_NotFound() {
        when(milestoneRepository.findByName("Nonexistent Milestone")).thenReturn(Optional.empty());

        Optional<Milestone> result = milestoneService.getMilestoneByName("Nonexistent Milestone");

        assertFalse(result.isPresent(), "Milestone should not be found by name");
    }

    @Test
    public void testGetMilestoneByStartDate_Success() {
        when(milestoneRepository.findByStartDate(startDate)).thenReturn(Arrays.asList(milestone));

        List<Milestone> result = milestoneService.getMilestoneByStartDate(startDate);

        assertNotNull(result, "Milestones should be found");
        assertEquals(1, result.size());
        assertEquals(milestone.getMilestoneId(), result.get(0).getMilestoneId());
    }

    @Test
    public void testGetMilestoneByCompletionDate_Success() {
        when(milestoneRepository.findByCompletionDate(completionDate)).thenReturn(Arrays.asList(milestone));

        List<Milestone> result = milestoneService.getMilestoneByCompletionDate(completionDate);

        assertNotNull(result, "Milestones should be found");
        assertEquals(1, result.size());
        assertEquals(milestone.getMilestoneId(), result.get(0).getMilestoneId());
    }

    @Test
    public void testGetMilestoneByStatus_Success() {
        when(milestoneRepository.findByStatus(Milestone.Status.ACTIVE)).thenReturn(Arrays.asList(milestone));

        List<Milestone> result = milestoneService.getMilestoneByStatus(Milestone.Status.ACTIVE);

        assertNotNull(result, "Milestones should be found with status ACTIVE");
        assertEquals(1, result.size());
        assertEquals(milestone.getMilestoneId(), result.get(0).getMilestoneId());
    }

    @Test
    public void testGetCustomerByMilestoneId_InvalidId() {
        int milestoneId = -1;

        when(milestoneRepository.findById(milestoneId)).thenThrow(new IllegalArgumentException("Invalid ID"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> milestoneService.getMilestoneByMilestoneId(milestoneId));

        assertTrue(exception.getMessage().contains("Invalid Milestone milestoneId: -1"));
        verify(milestoneRepository, times(1)).findById(milestoneId);

    }

    @Test
    public void testGetMilestoneByName_InvalidName() {
        String name = null;

        when(milestoneRepository.findByName(name)).thenThrow(new IllegalArgumentException("Invalid name"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> milestoneService.getMilestoneByName(name));

        assertTrue(exception.getMessage().contains("Invalid milestone name: null"));
        verify(milestoneRepository, times(1)).findByName(name);
    }

    @Test
    public void testGetMilestoneByStartDate_InvalidDate() {
        Date startDate = null;

        when(milestoneRepository.findByStartDate(startDate)).thenThrow(new IllegalArgumentException("Invalid start date"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> milestoneService.getMilestoneByStartDate(startDate));

        assertTrue(exception.getMessage().contains("Invalid start date: null"));
        verify(milestoneRepository, times(1)).findByStartDate(startDate);
    }

    @Test
    public void testGetMilestoneByCompletionDate_InvalidDate() {
        Date completionDate = null;

        when(milestoneRepository.findByCompletionDate(completionDate)).thenThrow(new IllegalArgumentException("Invalid completion date"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> milestoneService.getMilestoneByCompletionDate(completionDate));

        assertTrue(exception.getMessage().contains("Invalid completion date: null"));
        verify(milestoneRepository, times(1)).findByCompletionDate(completionDate);
    }

    @Test
    public void testGetMilestoneByStatus_InvalidStatus() {
        Enum status = null;

        when(milestoneRepository.findByStatus(status)).thenThrow(new IllegalArgumentException("Invalid status"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> milestoneService.getMilestoneByStatus(status));

        assertTrue(exception.getMessage().contains("Invalid status: null"));
        verify(milestoneRepository, times(1)).findByStatus(status);
    }
}
