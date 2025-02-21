package com.example.savings_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.savings_app.model.Account;
import com.example.savings_app.repository.AccountRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

/** TUnit tests for the AccountService. */
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  // Mock the AccountRepository and ParentChildAccountService dependencies
  @Mock private AccountRepository accountRepository;
  private AccountService accountService;
  @Mock private ParentChildAccountService parentChildAccountService;

  // Constants used in tests
  private final int USER_ID = 1;
  private final int INVALID_USER_ID = 99;

  private static final String VALID_EMAIL = "test@example.com";
  private static final String INVALID_EMAIL = "invalid@example.com";

  private static final LocalDate NOW = LocalDate.parse("2024-11-16");

  /** Setup method to initialize the AccountService before each test. */
  @BeforeEach
  void setUp() {
    accountService = new AccountService(accountRepository, parentChildAccountService);
  }

  /**
   * This is a manual test for saving an account in the repository. It is used for testing the
   * actual saving operation in the database.
   */
  @Transactional
  @Commit
  @Test
  void manualTestSaveAccount() {
    // Create an account to test save functionality
    Account newAccount = validCreateAccount;
    accountRepository.save(newAccount);
    accountRepository.flush(); // Flush changes to the DB
  }

  /**
   * Test for fetching an account by user ID when the account exists. It verifies that the correct
   * account is retrieved from the repository.
   */
  @Test
  void getAccountByUserId_ShouldReturnAccount_WhenExists() {
    // Setup mock data for an account
    Account account = validAccount;

    // Mock the repository's behavior
    when(accountRepository.findById(USER_ID)).thenReturn(Optional.of(account));

    // Call the service method
    Optional<Account> retrievedAccount = accountService.getAccountByUserId(USER_ID);

    // Verify the account was retrieved correctly
    assertTrue(retrievedAccount.isPresent());
    assertEquals(account.getUserId(), retrievedAccount.get().getUserId());
    verify(accountRepository, times(1)).findById(USER_ID);
  }

  /**
   * Test for fetching an account by user ID when the account does not exist. It verifies that an
   * empty Optional is returned.
   */
  @Test
  void getAccountByUserId_ShouldReturnEmpty_WhenNotExists() {
    // Mock the repository's behavior for a non-existing account
    when(accountRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

    // Call the service method
    Optional<Account> retrievedAccount = accountService.getAccountByUserId(INVALID_USER_ID);

    // Verify that no account is retrieved
    assertFalse(retrievedAccount.isPresent());
    verify(accountRepository, times(1)).findById(INVALID_USER_ID);
  }

  /**
   * Test for creating a new account when the account is valid. Verifies the correct saving of the
   * new account.
   */
  @Transactional
  @Test
  void createAccount_ShouldReturnSavedAccount_WhenValidAccountProvided() {
    // Create a valid account for testing
    Account newAccount = validCreateAccount;

    // Mock the repository's behavior to ensure no account exists with the given email
    when(accountRepository.findByEmail(newAccount.getEmail())).thenReturn(Optional.empty());
    when(accountRepository.save(newAccount)).thenReturn(newAccount);

    // Call the service method
    Account savedAccount = accountService.createAccount(newAccount);

    // Verify the account was saved correctly
    assertNotNull(savedAccount);
    assertEquals(newAccount.getEmail(), savedAccount.getEmail());
    verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
    verify(accountRepository, times(1)).save(newAccount);
  }

  /**
   * Test for handling the case when the first name is missing in account creation. It should throw
   * an IllegalArgumentException.
   */
  @Test
  void createAccount_ShouldThrowException_WhenFirstNameIsMissing() {
    // Create an account without a first name
    Account newAccount =
        Account.builder()
            .lastName("Smith")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.child)
            .build();

    // Assert that an exception is thrown
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> accountService.createAccount(newAccount));

    // Verify the exception message
    assertEquals("First Name is required", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  /**
   * Test for handling the case when the last name is missing in account creation. It should throw
   * an IllegalArgumentException.
   */
  @Test
  void createAccount_ShouldThrowException_WhenLastNameIsMissing() {
    // Create an account without a last name
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.child)
            .build();

    // Assert that an exception is thrown
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> accountService.createAccount(newAccount));

    // Verify the exception message
    assertEquals("Last Name is required", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  /**
   * Test for handling the case when the email is missing in account creation. It should throw an
   * IllegalArgumentException.
   */
  @Test
  void createAccount_ShouldThrowException_WhenEmailIsMissing() {
    // Create an account without an email
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .passwordHash("password")
            .role(Account.Role.child)
            .build();

    // Assert that an exception is thrown
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> accountService.createAccount(newAccount));

    // Verify the exception message
    assertEquals("Email is required", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  /**
   * Test for handling the case when the password is missing in account creation. It should throw an
   * IllegalArgumentException.
   */
  @Test
  void createAccount_ShouldThrowException_WhenPasswordIsMissing() {
    // Create an account without a password
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .email("test123@example.com")
            .role(Account.Role.child)
            .build();

    // Assert that an exception is thrown
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> accountService.createAccount(newAccount));

    // Verify the exception message
    assertEquals("Password is required", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  /**
   * Test for handling the case when the email already exists in the repository. It should throw an
   * IllegalStateException.
   */
  @Test
  void createAccount_ShouldThrowException_WhenEmailAlreadyExists() {
    // Mock existing account with the same email
    Account existingAccount = Account.builder().email("test123@example.com").build();

    // Create a new account with the same email
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.child)
            .dob(LocalDate.parse("1999-11-10"))
            .build();

    // Mock repository to return the existing account for the email
    when(accountRepository.findByEmail(newAccount.getEmail()))
        .thenReturn(Optional.of(existingAccount));

    // Assert that an exception is thrown
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> accountService.createAccount(newAccount));

    // Verify the exception message
    assertEquals("An account with this email already exists", exception.getMessage());
    verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
    verify(accountRepository, never()).save(any());
  }

  /**
   * Test for handling data integrity violation when trying to create an account. It simulates a
   * case where a DataIntegrityViolationException occurs.
   */
  @Test
  void createAccount_ShouldThrowException_WhenDataIntegrityViolationOccurs() {
    // Create a valid account for testing
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.child)
            .dob(LocalDate.parse("1999-11-10"))
            .build();

    // Mock repository behavior for finding the email and saving the account
    when(accountRepository.findByEmail(newAccount.getEmail())).thenReturn(Optional.empty());
    when(accountRepository.save(newAccount))
        .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate entry"));

    // Assert that an exception is thrown
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> accountService.createAccount(newAccount));

    // Verify the exception message
    assertTrue(
        exception.getMessage().contains("Failed to create account due to data integrity issues"));
    verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
    verify(accountRepository, times(1)).save(newAccount);
  }

  /**
   * Test for deleting an account by user ID. It verifies that the account deletion happens
   * correctly.
   */
  @Test
  void testDeleteAccount_Success() {
    int userId = 1;

    // Call the service method to delete the account
    accountService.deleteAccount(userId);

    // Verify that the accountRepository's deleteById method was called once
    verify(accountRepository, times(1)).deleteById(userId);
  }

  /**
   * Test for handling invalid user ID when trying to delete an account. It ensures that an
   * IllegalArgumentException is thrown when the ID is invalid.
   */
  @Test
  void testDeleteAccount_InvalidUserId_ThrowsException() {
    int invalidUserId = -1;
    doThrow(new IllegalArgumentException("Invalid user ID"))
        .when(accountRepository)
        .deleteById(invalidUserId);

    // Assert that an exception is thrown
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> accountService.deleteAccount(invalidUserId));

    // Verify the exception message
    assertEquals("Invalid user ID", exception.getMessage());
    verify(accountRepository, times(1)).deleteById(invalidUserId);
  }

  /**
   * Test for updating an account with new information. It ensures that the updated account is saved
   * correctly.
   */
  @Test
  void testUpdateAccount_SuccessfulUpdate() {
    int userId = 1;
    Account existingAccount =
        new Account(
            userId,
            "John",
            "Doe",
            "john.doe@example.com",
            "hashed_password",
            Account.Role.parent,
            null,
            NOW,
            LocalDate.parse("1999-11-10"));
    Account updatedAccount =
        new Account(
            userId,
            "John",
            "Smith",
            "john.smith@example.com",
            "new_hashed_password",
            Account.Role.parent,
            2,
            NOW,
            LocalDate.parse("1999-11-10"));

    // Mock the repository's behavior
    when(accountRepository.findById(userId)).thenReturn(Optional.of(existingAccount));

    // Call the service method
    Optional<Account> result = accountService.updateAccount(userId, updatedAccount);

    // Verify the updated account is returned
    assertTrue(result.isPresent());
    Account savedAccount = result.get();

    assertEquals("Smith", savedAccount.getLastName());
    assertEquals("john.smith@example.com", savedAccount.getEmail());
    assertEquals("new_hashed_password", savedAccount.getPasswordHash());
    assertEquals(Account.Role.parent, savedAccount.getRole());
    assertEquals(2, savedAccount.getChildId());

    // Verify that the save method was called
    verify(accountRepository, times(1)).save(existingAccount);
  }

  /**
   * Test for updating an account with no changes. It ensures that no save operation occurs when no
   * changes are made.
   */
  @Test
  void testUpdateAccount_NoChanges() {
    int userId = 1;
    Account existingAccount =
        new Account(
            userId,
            "John",
            "Doe",
            "john.doe@example.com",
            "hashed_password",
            Account.Role.parent,
            null,
            NOW,
            LocalDate.parse("1999-11-10"));
    Account updatedAccount =
        new Account(
            userId,
            "John",
            "Doe",
            "john.doe@example.com",
            "hashed_password",
            Account.Role.parent,
            null,
            NOW,
            LocalDate.parse("1999-11-10"));

    // Mock the repository's behavior
    when(accountRepository.findById(userId)).thenReturn(Optional.of(existingAccount));

    // Call the service method
    Optional<Account> result = accountService.updateAccount(userId, updatedAccount);

    // Verify that the result is returned and no save operation occurs
    assertTrue(result.isPresent());
    verify(accountRepository, never()).save(existingAccount);
  }

  /**
   * Test for handling the case when the account to be updated is not found. It ensures that no
   * update occurs if the account is not found in the repository.
   */
  @Test
  void testUpdateAccount_AccountNotFound() {
    int userId = 1;
    Account updatedAccount =
        new Account(
            userId,
            "John",
            "Smith",
            "john.smith@example.com",
            "new_hashed_password",
            Account.Role.child,
            2,
            NOW,
            LocalDate.parse("1999-11-10"));

    // Mock the repository's behavior to return empty for the userId
    when(accountRepository.findById(userId)).thenReturn(Optional.empty());

    // Call the service method
    Optional<Account> result = accountService.updateAccount(userId, updatedAccount);

    // Verify that the result is empty and no save occurs
    assertTrue(result.isEmpty());
    verify(accountRepository, never()).save(any(Account.class));
  }

  /**
   * Test for fetching an account by email when the account exists. It verifies the correct account
   * is returned by email.
   */
  @Test
  void getAccountByEmail_ShouldReturnAccount_WhenExists() {
    // Mock repository to return the account for the valid email
    when(accountRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(validAccount));

    // Call the service method
    Optional<Account> retrievedAccount = accountService.getAccountByEmail(VALID_EMAIL);

    // Verify the account was retrieved correctly
    assertTrue(retrievedAccount.isPresent());
    assertEquals(validAccount.getEmail(), retrievedAccount.get().getEmail());
    verify(accountRepository, times(1)).findByEmail(VALID_EMAIL);
  }

  /**
   * Test for fetching an account by email when the account does not exist. It ensures that an empty
   * Optional is returned when no account is found by email.
   */
  @Test
  void getAccountByEmail_ShouldReturnEmpty_WhenNotExists() {
    // Mock repository to return empty for invalid email
    when(accountRepository.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

    // Call the service method
    Optional<Account> retrievedAccount = accountService.getAccountByEmail(INVALID_EMAIL);

    // Verify that no account is returned
    assertFalse(retrievedAccount.isPresent());
    verify(accountRepository, times(1)).findByEmail(INVALID_EMAIL);
  }

  /**
   * Test for handling unexpected errors when fetching an account by email. It ensures that a
   * RuntimeException is thrown in case of an error.
   */
  @Test
  void getAccountByEmail_ShouldThrowException_WhenUnexpectedErrorOccurs() {
    // Mock repository to throw an unexpected error
    when(accountRepository.findByEmail(VALID_EMAIL))
        .thenThrow(new RuntimeException("Unexpected error"));

    // Assert that the error is caught
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> accountService.getAccountByEmail(VALID_EMAIL));

    // Verify the exception message
    assertTrue(exception.getMessage().contains("Failed to retrieve account with email"));
    verify(accountRepository, times(1)).findByEmail(VALID_EMAIL);
  }

  // Valid test data used for account creation and verification
  private final Account validAccount =
      Account.builder()
          .userId(USER_ID)
          .firstName("John")
          .lastName("Smith")
          .email("test@example.com")
          .passwordHash("password")
          .role(Account.Role.parent)
          .dob(LocalDate.parse("1999-11-10"))
          .build();

  private final Account validCreateAccount =
      Account.builder()
          .firstName("Dave")
          .lastName("Smith")
          .email("test123@example.com")
          .passwordHash("password")
          .role(Account.Role.child)
          .dob(LocalDate.parse("1999-11-10"))
          .build();
}
