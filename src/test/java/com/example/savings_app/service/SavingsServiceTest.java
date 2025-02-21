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

/** Unit tests for the SavingsService class. */
public class SavingsServiceTest {

  private SavingsRepository savingsRepository; // Mocked savings repository
  private SavingsService savingsService; // The service under test
  private AccountService accountService; // Mocked account service

  private Savings savings; // Test savings object
  private final LocalDate savingsDate = LocalDate.parse("2024-11-01"); // Sample date for savings
  private Account user; // Test user account

  /** Set up the test environment before each test. Initializes mocked services and sample data. */
  @BeforeEach
  public void setUp() {
    savingsRepository = mock(SavingsRepository.class); // Mock the savings repository
    accountService = mock(AccountService.class); // Mock the account service
    savingsService =
        new SavingsService(savingsRepository, accountService); // Initialize service under test

    // Initialize sample data for savings and account
    savings =
        Savings.builder()
            .savingsId(1)
            .amount(BigDecimal.valueOf(150.00))
            .date(savingsDate)
            .milestoneId(1)
            .build();

    user = new Account();
    user.setUserId(1); // Set sample user ID
  }

  /** Test case for finding savings by ID when the savings entry exists. */
  @Test
  public void testFindById_Success() {
    when(savingsRepository.findById(1))
        .thenReturn(Optional.of(savings)); // Mock findById to return savings

    Optional<Savings> result = savingsService.getSavingsById(1); // Call service method

    // Assert that savings were found
    assertTrue(result.isPresent(), "Savings should be found");
    assertEquals(savings.getSavingsId(), result.get().getSavingsId(), "Savings ID should match");
    verify(savingsRepository, times(1)).findById(1); // Verify that findById was called once
  }

  /** Test case for finding savings by ID when no entry is found. */
  @Test
  public void testFindById_NotFound() {
    when(savingsRepository.findById(99))
        .thenReturn(Optional.empty()); // Mock findById to return empty

    Optional<Savings> result = savingsService.getSavingsById(99); // Call service method

    // Assert that no savings were found
    assertFalse(result.isPresent(), "Savings should not be found");
    verify(savingsRepository, times(1)).findById(99); // Verify that findById was called once
  }

  /** Test case for getting savings by a specific date. */
  @Test
  public void testGetSavingsByDate_Success() {
    when(savingsRepository.findByDate(savingsDate))
        .thenReturn(Arrays.asList(savings)); // Mock findByDate

    List<Savings> result = savingsService.getSavingsByDate(savingsDate); // Call service method

    // Assert that savings were found and returned
    assertNotNull(result, "Result should not be null");
    assertEquals(1, result.size(), "Should return one Savings entry");
    assertEquals(savings.getSavingsId(), result.get(0).getSavingsId(), "Savings ID should match");
    verify(savingsRepository, times(1)).findByDate(savingsDate); // Verify method call
  }

  /** Test case for getting savings by date when no savings match. */
  @Test
  public void testGetSavingsByDate_NoResults() {
    when(savingsRepository.findByDate(savingsDate)).thenReturn(Arrays.asList()); // Mock empty list

    List<Savings> result = savingsService.getSavingsByDate(savingsDate); // Call service method

    // Assert that result is empty
    assertNotNull(result, "Result should not be null");
    assertTrue(result.isEmpty(), "Result should be empty");
    verify(savingsRepository, times(1)).findByDate(savingsDate); // Verify method call
  }

  /** Test case for getting savings by milestone ID when the milestone exists. */
  @Test
  public void testGetSavingsByMilestoneId_Success() {
    when(savingsRepository.findByMilestoneId(1))
        .thenReturn(Optional.of(savings)); // Mock findByMilestoneId

    Optional<Savings> result = savingsService.getSavingsByMilestoneId(1); // Call service method

    // Assert that savings with the given milestone ID were found
    assertTrue(result.isPresent(), "Savings should be found");
    assertEquals(
        savings.getMilestoneId(), result.get().getMilestoneId(), "Milestone ID should match");
    verify(savingsRepository, times(1)).findByMilestoneId(1); // Verify method call
  }

  /** Test case for getting savings by milestone ID when no savings match. */
  @Test
  public void testGetSavingsByMilestoneId_NotFound() {
    when(savingsRepository.findByMilestoneId(99)).thenReturn(Optional.empty()); // Mock empty result

    Optional<Savings> result = savingsService.getSavingsByMilestoneId(99); // Call service method

    // Assert that no savings were found
    assertFalse(result.isPresent(), "Savings should not be found");
    verify(savingsRepository, times(1)).findByMilestoneId(99); // Verify method call
  }

  /** Test case for successfully deleting savings by ID. */
  @Test
  public void deleteSavings_Success() {
    int savingsId = 1;
    doNothing().when(savingsRepository).deleteById(savingsId); // Mock deleteById

    savingsService.deleteSavings(savingsId); // Call service method

    // Verify that deleteById was called once
    verify(savingsRepository, times(1)).deleteById(savingsId);
  }

  /** Test case for handling invalid ID when deleting savings. */
  @Test
  public void deleteSavings_InvalidId() {
    int savingsId = 1;
    doThrow(new IllegalArgumentException("Invalid Savings Id: " + savingsId))
        .when(savingsRepository)
        .deleteById(savingsId); // Mock throwing exception on deleteById

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              savingsService.deleteSavings(savingsId); // Call service method
            });

    // Assert that the exception message matches the expected error
    assertEquals("Invalid Savings Id: 1", exception.getMessage());
    verify(savingsRepository, times(1)).deleteById(savingsId); // Verify method call
  }

  /** Test case for getting all savings for a specific user. */
  @Test
  public void testGetAllSavingsForUser_Success() {
    when(savingsRepository.findAllByUser(user))
        .thenReturn(Arrays.asList(savings)); // Mock findAllByUser

    List<Savings> result = savingsService.getAllSavingsForUser(user); // Call service method

    // Assert that savings for the user were returned
    assertNotNull(result, "Savings should be found for user");
    assertEquals(1, result.size(), "There should be one Saving for the user");
    assertEquals(
        savings.getSavingsId(), result.get(0).getSavingsId(), "The milestone ID should match");
  }

  /** Test case for handling invalid account when getting all savings. */
  @Test
  public void testGetAllSavingsForUser_InvalidAccount() {
    when(savingsRepository.findAllByUser(null))
        .thenThrow(
            new IllegalArgumentException(
                "Invalid Account Provided")); // Mock exception on null user

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> savingsService.getAllSavingsForUser(null)); // Call service method

    // Assert that the exception message matches the expected error
    assertTrue(exception.getMessage().contains("Invalid Account Provided"));
  }

  /** Test case for handling an exception when retrieving savings for a user. */
  @Test
  public void testGetAllSavingsForUser_ExceptionThrown() {
    when(savingsRepository.findAllByUser(user))
        .thenThrow(new RuntimeException("Database error")); // Mock exception

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> savingsService.getAllSavingsForUser(user)); // Call service method

    // Assert that the exception message matches the expected error
    assertTrue(exception.getMessage().contains("Failed to retrieve Savings with Account provided"));
  }

  /** Test case for successfully creating a new milestone savings. */
  @Test
  public void testCreateMilestoneSuccess() {
    Savings savings1 =
        Savings.builder()
            .savingsId(1)
            .user(user)
            .amount(BigDecimal.valueOf(150.00))
            .date(savingsDate)
            .milestoneId(1)
            .build();

    when(accountService.getAccountByUserId(user.getUserId()))
        .thenReturn(Optional.of(user)); // Mock account lookup
    when(savingsRepository.save(any(Savings.class)))
        .thenReturn(savings1); // Mock saving the savings

    Savings createdSavings = savingsService.createSavings(savings1); // Call service method

    // Assert that the savings were created successfully
    assertNotNull(createdSavings);
    assertEquals(1, savings1.getMilestoneId());
    verify(savingsRepository, times(1)).save(savings1); // Verify save method was called
  }
}
