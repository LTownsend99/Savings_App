package com.example.savings_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.example.savings_app.exception.MilestoneException;
import com.example.savings_app.model.Account;
import com.example.savings_app.model.Milestone;
import com.example.savings_app.repository.MilestoneRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit test class for MilestoneService to test its methods and ensure proper functionality. */
@ExtendWith(MockitoExtension.class)
public class MilestoneServiceTest {

  private MilestoneRepository milestoneRepository;
  private MilestoneService milestoneService;
  private AccountService accountService;
  private Account user;
  private Milestone milestone;
  private LocalDate startDate;
  private LocalDate completionDate;

  /** Setup method to initialize the mocks and test data before each test case. */
  @BeforeEach
  public void setUp() {
    accountService = mock(AccountService.class);
    milestoneRepository = mock(MilestoneRepository.class);
    milestoneService = new MilestoneService(milestoneRepository, accountService);

    // Initialize test data for milestones and user account
    startDate = LocalDate.parse("2024-11-01");
    completionDate = LocalDate.parse("2024-11-01");
    milestone =
        Milestone.builder()
            .milestoneId(1)
            .milestoneName("Milestone")
            .targetAmount(BigDecimal.valueOf(200.00))
            .savedAmount(BigDecimal.valueOf(50.00))
            .startDate(startDate)
            .completionDate(completionDate)
            .status(Milestone.Status.active)
            .build();

    user = new Account();
    user.setUserId(1);
  }

  /** Test case to verify successful retrieval of milestone by ID. */
  @Test
  public void testGetCustomerByMilestoneId_Success() {
    when(milestoneRepository.findById(1)).thenReturn(Optional.of(milestone));

    Optional<Milestone> result = milestoneService.getMilestoneByMilestoneId(1);

    assertTrue(result.isPresent(), "Milestone should be found");
    assertEquals(milestone.getMilestoneId(), result.get().getMilestoneId());
  }

  /** Test case for unsuccessful retrieval of milestone by ID (Milestone not found). */
  @Test
  public void testGetCustomerByMilestoneId_NotFound() {
    when(milestoneRepository.findById(1)).thenReturn(Optional.empty());

    Optional<Milestone> result = milestoneService.getMilestoneByMilestoneId(1);

    assertFalse(result.isPresent(), "Milestone should not be found");
  }

  /** Test case to verify successful retrieval of milestone by its name. */
  @Test
  public void testGetMilestoneByName_Success() {
    when(milestoneRepository.findByMilestoneName("Test Milestone"))
        .thenReturn(Optional.of(milestone));

    Optional<Milestone> result = milestoneService.getMilestoneByName("Test Milestone");

    assertTrue(result.isPresent(), "Milestone should be found by name");
    assertEquals("Milestone", result.get().getMilestoneName());
  }

  /** Test case for unsuccessful retrieval of milestone by name (Milestone not found). */
  @Test
  public void testGetMilestoneByName_NotFound() {
    when(milestoneRepository.findByMilestoneName("Nonexistent Milestone"))
        .thenReturn(Optional.empty());

    Optional<Milestone> result = milestoneService.getMilestoneByName("Nonexistent Milestone");

    assertFalse(result.isPresent(), "Milestone should not be found by name");
  }

  /** Test case to verify retrieval of milestones by their start date. */
  @Test
  public void testGetMilestoneByStartDate_Success() {
    when(milestoneRepository.findByStartDate(startDate)).thenReturn(Arrays.asList(milestone));

    List<Milestone> result = milestoneService.getMilestoneByStartDate(startDate);

    assertNotNull(result, "Milestones should be found");
    assertEquals(1, result.size());
    assertEquals(milestone.getMilestoneId(), result.get(0).getMilestoneId());
  }

  /** Test case to verify retrieval of milestones by their completion date. */
  @Test
  public void testGetMilestoneByCompletionDate_Success() {
    when(milestoneRepository.findByCompletionDate(completionDate))
        .thenReturn(Arrays.asList(milestone));

    List<Milestone> result = milestoneService.getMilestoneByCompletionDate(completionDate);

    assertNotNull(result, "Milestones should be found");
    assertEquals(1, result.size());
    assertEquals(milestone.getMilestoneId(), result.get(0).getMilestoneId());
  }

  /** Test case to verify retrieval of milestones by their status (active). */
  @Test
  public void testGetMilestoneByStatus_Success() {
    when(milestoneRepository.findByStatus(Milestone.Status.active))
        .thenReturn(Arrays.asList(milestone));

    List<Milestone> result = milestoneService.getMilestoneByStatus(Milestone.Status.active);

    assertNotNull(result, "Milestones should be found with status active");
    assertEquals(1, result.size());
    assertEquals(milestone.getMilestoneId(), result.get(0).getMilestoneId());
  }

  /** Test case for handling invalid milestone ID (negative ID). */
  @Test
  public void testGetCustomerByMilestoneId_InvalidId() {
    int milestoneId = -1;

    when(milestoneRepository.findById(milestoneId))
        .thenThrow(new IllegalArgumentException("Invalid ID"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> milestoneService.getMilestoneByMilestoneId(milestoneId));

    assertTrue(exception.getMessage().contains("Invalid Milestone milestoneId: -1"));
    verify(milestoneRepository, times(1)).findById(milestoneId);
  }

  /** Test case for handling invalid milestone name (null value). */
  @Test
  public void testGetMilestoneByName_InvalidName() {
    String name = null;

    when(milestoneRepository.findByMilestoneName(name))
        .thenThrow(new IllegalArgumentException("Invalid name"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> milestoneService.getMilestoneByName(name));

    assertTrue(exception.getMessage().contains("Invalid milestone name: null"));
    verify(milestoneRepository, times(1)).findByMilestoneName(name);
  }

  /** Test case for handling invalid milestone start date (null value). */
  @Test
  public void testGetMilestoneByStartDate_InvalidDate() {
    LocalDate startDate = null;

    when(milestoneRepository.findByStartDate(startDate))
        .thenThrow(new IllegalArgumentException("Invalid start date"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> milestoneService.getMilestoneByStartDate(startDate));

    assertTrue(exception.getMessage().contains("Invalid start date: null"));
    verify(milestoneRepository, times(1)).findByStartDate(startDate);
  }

  /** Test case for handling invalid milestone completion date (null value). */
  @Test
  public void testGetMilestoneByCompletionDate_InvalidDate() {
    LocalDate completionDate = null;

    when(milestoneRepository.findByCompletionDate(completionDate))
        .thenThrow(new IllegalArgumentException("Invalid completion date"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> milestoneService.getMilestoneByCompletionDate(completionDate));

    assertTrue(exception.getMessage().contains("Invalid completion date: null"));
    verify(milestoneRepository, times(1)).findByCompletionDate(completionDate);
  }

  /** Test case for handling invalid milestone status (null value). */
  @Test
  public void testGetMilestoneByStatus_InvalidStatus() {
    Enum status = null;

    when(milestoneRepository.findByStatus(status))
        .thenThrow(new IllegalArgumentException("Invalid status"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> milestoneService.getMilestoneByStatus(status));

    assertTrue(exception.getMessage().contains("Invalid status: null"));
    verify(milestoneRepository, times(1)).findByStatus(status);
  }

  /** Test case for creating a new milestone successfully. */
  @Test
  public void testCreateMilestoneSuccess() {
    Account user = new Account();
    user.setUserId(1);

    Milestone milestone =
        Milestone.builder()
            .user(user)
            .milestoneName("Buy a Bicycle")
            .targetAmount(new BigDecimal("100.00"))
            .startDate(LocalDate.now())
            .status(Milestone.Status.active)
            .build();

    when(accountService.getAccountByUserId(user.getUserId())).thenReturn(Optional.of(user));
    when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);

    Milestone createdMilestone = milestoneService.createMilestone(milestone);

    assertNotNull(createdMilestone);
    assertEquals("Buy a Bicycle", createdMilestone.getMilestoneName());
    verify(milestoneRepository, times(1)).save(milestone);
  }

  /** Test case to mark milestone as completed successfully. */
  @Test
  public void testMarkMilestoneAsCompleted_Success() {

    when(milestoneRepository.findById(1)).thenReturn(Optional.of(milestone));
    when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);

    Milestone updatedMilestone = milestoneService.markMilestoneAsCompleted(1);

    assertNotNull(updatedMilestone);
    assertEquals(Milestone.Status.completed, updatedMilestone.getStatus());
    verify(milestoneRepository, times(1)).save(updatedMilestone);
  }

  /** Test case for marking a milestone as completed when it's already completed. */
  @Test
  public void testMarkMilestoneAsCompleted_AlreadyCompleted() {
    Milestone milestoneCompleted = new Milestone();
    milestoneCompleted.setMilestoneId(1);
    milestoneCompleted.setStatus(Milestone.Status.completed);

    when(milestoneRepository.findById(1)).thenReturn(Optional.of(milestoneCompleted));

    assertThrows(IllegalStateException.class, () -> milestoneService.markMilestoneAsCompleted(1));
  }

  /** Test case when milestone is not found during the mark as completed operation. */
  @Test
  public void testMarkMilestoneAsCompleted_NotFound() {
    when(milestoneRepository.findById(-1)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class, () -> milestoneService.markMilestoneAsCompleted(-1));
  }

  /** Test case for updating the saved amount and checking if the milestone is completed. */
  @Test
  public void testUpdateSavedAmountAndCheckCompletion_invalidAmount() {
    Integer milestoneId = 1;

    Exception exception =
        assertThrows(
            MilestoneException.InvalidAmountException.class,
            () -> {
              milestoneService.updateSavedAmountAndCheckCompletion(
                  milestoneId, new BigDecimal("-100"));
            });

    assertEquals("The added amount must be greater than zero.", exception.getMessage());
  }

  /** Test case for handling milestone not found during the update of saved amount. */
  @Test
  public void testUpdateSavedAmountAndCheckCompletion_milestoneNotFound() {
    Integer milestoneId = 1;
    when(milestoneRepository.findById(milestoneId)).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            MilestoneException.MilestoneNotFoundException.class,
            () -> {
              milestoneService.updateSavedAmountAndCheckCompletion(
                  milestoneId, new BigDecimal("100"));
            });

    assertEquals("Milestone not found for id: 1", exception.getMessage());
  }

  /** Test case for retrieving all milestones for a user. */
  @Test
  public void testGetAllMilestonesForUser_Success() {
    when(milestoneRepository.findAllByUser(user)).thenReturn(Arrays.asList(milestone));

    List<Milestone> result = milestoneService.getAllMilestonesForUser(user);

    assertNotNull(result, "Milestones should be found for user");
    assertEquals(1, result.size(), "There should be one milestone for the user");
    assertEquals(
        milestone.getMilestoneId(),
        result.get(0).getMilestoneId(),
        "The milestone ID should match");
  }

  /** Test case for handling invalid user account when retrieving milestones. */
  @Test
  public void testGetAllMilestonesForUser_InvalidAccount() {
    when(milestoneRepository.findAllByUser(null))
        .thenThrow(new IllegalArgumentException("Invalid Account Provided"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> milestoneService.getAllMilestonesForUser(null));

    assertTrue(exception.getMessage().contains("Invalid Account Provided"));
  }

  /** Test case for handling exceptions thrown when retrieving milestones for a user. */
  @Test
  public void testGetAllMilestonesForUser_ExceptionThrown() {
    when(milestoneRepository.findAllByUser(user)).thenThrow(new RuntimeException("Database error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> milestoneService.getAllMilestonesForUser(user));

    assertTrue(
        exception.getMessage().contains("Failed to retrieve Milestone with Account provided"));
  }
}
