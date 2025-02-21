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

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  @Mock private AccountRepository accountRepository;
  private AccountService accountService;
  @Mock private ParentChildAccountService parentChildAccountService;

  private final int USER_ID = 1;
  private final int INVALID_USER_ID = 99;

  private static final String VALID_EMAIL = "test@example.com";
  private static final String INVALID_EMAIL = "invalid@example.com";

  private static final LocalDate NOW = LocalDate.parse("2024-11-16");

  @BeforeEach
  void setUp() {
    accountService = new AccountService(accountRepository, parentChildAccountService);
  }

  @Transactional
  @Commit
  @Test
  void manualTestSaveAccount() {
    Account newAccount = validCreateAccount;
    accountRepository.save(newAccount);
    accountRepository.flush();
  }

  @Test
  void getAccountByUserId_ShouldReturnAccount_WhenExists() {
    Account account = validAccount;

    when(accountRepository.findById(USER_ID)).thenReturn(Optional.of(account));

    Optional<Account> retrievedAccount = accountService.getAccountByUserId(USER_ID);

    assertTrue(retrievedAccount.isPresent());
    assertEquals(account.getUserId(), retrievedAccount.get().getUserId());
    verify(accountRepository, times(1)).findById(USER_ID);
  }

  @Test
  void getAccountByUserId_ShouldReturnEmpty_WhenNotExists() {

    when(accountRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

    Optional<Account> retrievedAccount = accountService.getAccountByUserId(INVALID_USER_ID);

    assertFalse(retrievedAccount.isPresent());
    verify(accountRepository, times(1)).findById(INVALID_USER_ID);
  }

  @Transactional
  @Test
  void createAccount_ShouldReturnSavedAccount_WhenValidAccountProvided() {

    Account newAccount = validCreateAccount;

    when(accountRepository.findByEmail(newAccount.getEmail())).thenReturn(Optional.empty());
    when(accountRepository.save(newAccount)).thenReturn(newAccount);

    Account savedAccount = accountService.createAccount(newAccount);

    assertNotNull(savedAccount);
    assertEquals(newAccount.getEmail(), savedAccount.getEmail());
    verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
    verify(accountRepository, times(1)).save(newAccount);
  }

  @Transactional
  @Test
  void createAccount_ShouldReturnSavedAccount_WhenValidAccountWithChildIdProvided() {

    Account newAccount = validCreateAccountWithChildId;

    when(accountRepository.findByEmail(newAccount.getEmail())).thenReturn(Optional.empty());
    when(accountRepository.save(newAccount)).thenReturn(newAccount);

    Account savedAccount = accountService.createAccount(newAccount);

    assertNotNull(savedAccount);
    assertEquals(newAccount.getEmail(), savedAccount.getEmail());
    verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
    verify(accountRepository, times(1)).save(newAccount);
  }

  @Test
  void createAccount_ShouldReturnSavedAccount_WhenValidChildIdProvided() {

    Account account =
        Account.builder()
            .userId(USER_ID)
            .firstName("John")
            .lastName("Smith")
            .email("test@example.com")
            .passwordHash("password")
            .role(Account.Role.parent)
            .childId(3)
            .dob(LocalDate.parse("1999-11-10"))
            .build();

    when(accountRepository.findByEmail(account.getEmail())).thenReturn(Optional.empty());
    when(accountRepository.save(account)).thenReturn(account);

    Account savedAccount = accountService.createAccount(account);

    assertNotNull(savedAccount);
    assertEquals(account.getEmail(), savedAccount.getEmail());
    verify(accountRepository, times(1)).findByEmail(account.getEmail());
    verify(accountRepository, times(1)).save(account);
  }

  @Test
  void createAccount_ShouldThrowException_WhenFirstNameIsMissing() {
    Account newAccount =
        Account.builder()
            .lastName("Smith")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.child)
            .build();

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              accountService.createAccount(newAccount);
            });

    assertEquals("First Name is required", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void createAccount_ShouldThrowException_WhenLastNameIsMissing() {
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.child)
            .build();

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              accountService.createAccount(newAccount);
            });

    assertEquals("Last Name is required", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void createAccount_ShouldThrowException_WhenEmailIsMissing() {
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .passwordHash("password")
            .role(Account.Role.child)
            .build();

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              accountService.createAccount(newAccount);
            });

    assertEquals("Email is required", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void createAccount_ShouldThrowException_WhenPasswordIsMissing() {
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .email("test123@example.com")
            .role(Account.Role.child)
            .build();

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              accountService.createAccount(newAccount);
            });

    assertEquals("Password is required", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void createAccount_ShouldThrowException_WhenEmailAlreadyExists() {
    Account existingAccount = Account.builder().email("test123@example.com").build();

    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.child)
            .dob(LocalDate.parse("1999-11-10"))
            .build();

    when(accountRepository.findByEmail(newAccount.getEmail()))
        .thenReturn(Optional.of(existingAccount));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> {
              accountService.createAccount(newAccount);
            });

    assertEquals("An account with this email already exists", exception.getMessage());
    verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void createAccount_ShouldThrowException_WhenDataIntegrityViolationOccurs() {
    Account newAccount =
        Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.child)
            .dob(LocalDate.parse("1999-11-10"))
            .build();

    when(accountRepository.findByEmail(newAccount.getEmail())).thenReturn(Optional.empty());
    when(accountRepository.save(newAccount))
        .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate entry"));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> {
              accountService.createAccount(newAccount);
            });

    assertTrue(
        exception.getMessage().contains("Failed to create account due to data integrity issues"));
    verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
    verify(accountRepository, times(1)).save(newAccount);
  }

  @Test
  void testDeleteAccount_Success() {
    int userId = 1;

    accountService.deleteAccount(userId);

    verify(accountRepository, times(1)).deleteById(userId);
  }

  @Test
  void testDeleteAccount_InvalidUserId_ThrowsException() {

    int invalidUserId = -1;
    doThrow(new IllegalArgumentException("Invalid user ID"))
        .when(accountRepository)
        .deleteById(invalidUserId);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> accountService.deleteAccount(invalidUserId));

    assertEquals("Invalid user ID", exception.getMessage());
    verify(accountRepository, times(1)).deleteById(invalidUserId);
  }

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

    when(accountRepository.findById(userId)).thenReturn(Optional.of(existingAccount));

    Optional<Account> result = accountService.updateAccount(userId, updatedAccount);

    assertTrue(result.isPresent());
    Account savedAccount = result.get();

    assertEquals("Smith", savedAccount.getLastName());
    assertEquals("john.smith@example.com", savedAccount.getEmail());
    assertEquals("new_hashed_password", savedAccount.getPasswordHash());
    assertEquals(Account.Role.parent, savedAccount.getRole());
    assertEquals(2, savedAccount.getChildId());

    verify(accountRepository, times(1)).save(existingAccount);
  }

  @Test
  void testUpdateAccount_NoChanges() {
    // Arrange
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

    when(accountRepository.findById(userId)).thenReturn(Optional.of(existingAccount));

    // Act
    Optional<Account> result = accountService.updateAccount(userId, updatedAccount);

    // Assert
    assertTrue(result.isPresent());
    verify(accountRepository, never()).save(existingAccount); // Ensure no save was called
  }

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

    when(accountRepository.findById(userId)).thenReturn(Optional.empty());

    Optional<Account> result = accountService.updateAccount(userId, updatedAccount);

    assertTrue(result.isEmpty());
    verify(accountRepository, never()).save(any(Account.class));
  }

  @Test
  void getAccountByEmail_ShouldReturnAccount_WhenExists() {

    when(accountRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(validAccount));

    Optional<Account> retrievedAccount = accountService.getAccountByEmail(VALID_EMAIL);

    assertTrue(retrievedAccount.isPresent());
    assertEquals(validAccount.getEmail(), retrievedAccount.get().getEmail());
    verify(accountRepository, times(1)).findByEmail(VALID_EMAIL);
  }

  @Test
  void getAccountByEmail_ShouldReturnEmpty_WhenNotExists() {

    when(accountRepository.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

    Optional<Account> retrievedAccount = accountService.getAccountByEmail(INVALID_EMAIL);

    assertFalse(retrievedAccount.isPresent());
    verify(accountRepository, times(1)).findByEmail(INVALID_EMAIL);
  }

  @Test
  void getAccountByEmail_ShouldThrowException_WhenUnexpectedErrorOccurs() {
    when(accountRepository.findByEmail(VALID_EMAIL))
        .thenThrow(new RuntimeException("Unexpected error"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              accountService.getAccountByEmail(VALID_EMAIL);
            });

    assertTrue(exception.getMessage().contains("Failed to retrieve account with email"));
    verify(accountRepository, times(1)).findByEmail(VALID_EMAIL);
  }

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

  private final Account validCreateAccountWithChildId =
      Account.builder()
          .firstName("John")
          .lastName("Doe")
          .email("john.doe@example.com")
          .passwordHash("hashed_password")
          .role(Account.Role.parent)
          .dob(LocalDate.parse("1999-11-10"))
          .childId(2)
          .build();
}
