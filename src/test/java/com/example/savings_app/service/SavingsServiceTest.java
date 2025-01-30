package com.example.savings_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Savings;
import com.example.savings_app.repository.SavingsRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SavingsServiceTest {

  private SavingsRepository savingsRepository;
  private SavingsService savingsService;

  private Savings savings;
  private final LocalDate savingsDate = LocalDate.parse("2024-11-01");
  private Account user;

  @BeforeEach
  public void setUp() {
    savingsRepository = mock(SavingsRepository.class);
    savingsService = new SavingsService(savingsRepository);

    // Initialize test data
    savings =
        Savings.builder()
            .savingsId(1)
            .amount(BigDecimal.valueOf(150.00))
            .date(savingsDate)
            .milestoneId(1)
            .build();

    user = new Account();
    user.setUserId(1);
  }

  @Test
  public void testFindById_Success() {
    when(savingsRepository.findById(1)).thenReturn(Optional.of(savings));

    Optional<Savings> result = savingsService.getSavingsById(1);

    assertTrue(result.isPresent(), "Savings should be found");
    assertEquals(savings.getSavingsId(), result.get().getSavingsId());
    verify(savingsRepository, times(1)).findById(1);
  }

  @Test
  public void testFindById_NotFound() {
    when(savingsRepository.findById(99)).thenReturn(Optional.empty());

    Optional<Savings> result = savingsService.getSavingsById(99);

    assertFalse(result.isPresent(), "Savings should not be found");
    verify(savingsRepository, times(1)).findById(99);
  }

  @Test
  public void testGetSavingsByDate_Success() {
    when(savingsRepository.findByDate(savingsDate)).thenReturn(Arrays.asList(savings));

    List<Savings> result = savingsService.getSavingsByDate(savingsDate);

    assertNotNull(result, "Result should not be null");
    assertEquals(1, result.size(), "Should return one Savings entry");
    assertEquals(savings.getSavingsId(), result.get(0).getSavingsId());
    verify(savingsRepository, times(1)).findByDate(savingsDate);
  }

  @Test
  public void testGetSavingsByDate_NoResults() {
    when(savingsRepository.findByDate(savingsDate)).thenReturn(Arrays.asList());

    List<Savings> result = savingsService.getSavingsByDate(savingsDate);

    assertNotNull(result, "Result should not be null");
    assertTrue(result.isEmpty(), "Result should be empty");
    verify(savingsRepository, times(1)).findByDate(savingsDate);
  }

  @Test
  public void testGetSavingsByMilestoneId_Success() {
    when(savingsRepository.findByMilestoneId(1)).thenReturn(Optional.of(savings));

    Optional<Savings> result = savingsService.getSavingsByMilestoneId(1);

    assertTrue(result.isPresent(), "Savings should be found");
    assertEquals(savings.getMilestoneId(), result.get().getMilestoneId());
    verify(savingsRepository, times(1)).findByMilestoneId(1);
  }

  @Test
  public void testGetSavingsByMilestoneId_NotFound() {
    when(savingsRepository.findByMilestoneId(99)).thenReturn(Optional.empty());

    Optional<Savings> result = savingsService.getSavingsByMilestoneId(99);

    assertFalse(result.isPresent(), "Savings should not be found");
    verify(savingsRepository, times(1)).findByMilestoneId(99);
  }

  @Test
  public void deleteSavings_Success() {
    int savingsId = 1;
    doNothing().when(savingsRepository).deleteById(savingsId);

    savingsService.deleteSavings(savingsId);

    verify(savingsRepository, times(1)).deleteById(savingsId);
  }

  @Test
  public void deleteSavings_InvalidId() {
    int savingsId = 1;
    doThrow(new IllegalArgumentException("Invalid Savings Id: " + savingsId))
        .when(savingsRepository)
        .deleteById(savingsId);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              savingsService.deleteSavings(savingsId);
            });

    assertEquals("Invalid Savings Id: 1", exception.getMessage());
    verify(savingsRepository, times(1)).deleteById(savingsId);
  }

  @Test
  public void testGetAllSavingsForUser_Success() {
    when(savingsRepository.findAllByUser(user)).thenReturn(Arrays.asList(savings));

    List<Savings> result = savingsService.getAllSavingsForUser(user);

    assertNotNull(result, "Savings should be found for user");
    assertEquals(1, result.size(), "There should be one Saving for the user");
    assertEquals(
        savings.getSavingsId(), result.get(0).getSavingsId(), "The milestone ID should match");
  }

  @Test
  public void testGetAllSavingsForUser_InvalidAccount() {
    when(savingsRepository.findAllByUser(null))
        .thenThrow(new IllegalArgumentException("Invalid Account Provided"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> savingsService.getAllSavingsForUser(null));

    assertTrue(exception.getMessage().contains("Invalid Account Provided"));
  }

  @Test
  public void testGetAllSavingsForUser_ExceptionThrown() {
    when(savingsRepository.findAllByUser(user)).thenThrow(new RuntimeException("Database error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> savingsService.getAllSavingsForUser(user));

    assertTrue(exception.getMessage().contains("Failed to retrieve Savings with Account provided"));
  }
}
