package com.example.savings_app.service;

import com.example.savings_app.model.Account;
import com.example.savings_app.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    private int USER_ID = 1;
    private int INVALID_USER_ID = 99;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
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

    @Test
    void createAccount_ShouldThrowException_WhenFirstNameIsMissing() {
        Account newAccount = Account.builder()
                .lastName("Smith")
                .email("test123@example.com")
                .passwordHash("password")
                .role(Account.Role.CHILD)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(newAccount);
        });

        assertEquals("First Name is required", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccount_ShouldThrowException_WhenLastNameIsMissing() {
        Account newAccount = Account.builder()
                .firstName("Dave")
                .email("test123@example.com")
                .passwordHash("password")
                .role(Account.Role.CHILD)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(newAccount);
        });

        assertEquals("Last Name is required", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccount_ShouldThrowException_WhenEmailIsMissing() {
        Account newAccount = Account.builder()
                .firstName("Dave")
                .lastName("Smith")
                .passwordHash("password")
                .role(Account.Role.CHILD)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(newAccount);
        });

        assertEquals("Email is required", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccount_ShouldThrowException_WhenPasswordIsMissing() {
        Account newAccount = Account.builder()
                .firstName("Dave")
                .lastName("Smith")
                .email("test123@example.com")
                .role(Account.Role.CHILD)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(newAccount);
        });

        assertEquals("Password is required", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccount_ShouldThrowException_WhenEmailAlreadyExists() {
        Account existingAccount = Account.builder()
                .email("test123@example.com")
                .build();

        Account newAccount = Account.builder()
                .firstName("Dave")
                .lastName("Smith")
                .email("test123@example.com")
                .passwordHash("password")
                .role(Account.Role.CHILD)
                .build();

        when(accountRepository.findByEmail(newAccount.getEmail())).thenReturn(Optional.of(existingAccount));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            accountService.createAccount(newAccount);
        });

        assertEquals("An account with this email already exists", exception.getMessage());
        verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccount_ShouldThrowException_WhenDataIntegrityViolationOccurs() {
        Account newAccount = Account.builder()
                .firstName("Dave")
                .lastName("Smith")
                .email("test123@example.com")
                .passwordHash("password")
                .role(Account.Role.CHILD)
                .build();

        when(accountRepository.findByEmail(newAccount.getEmail())).thenReturn(Optional.empty());
        when(accountRepository.save(newAccount)).thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate entry"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            accountService.createAccount(newAccount);
        });

        assertTrue(exception.getMessage().contains("Failed to create account due to data integrity issues"));
        verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
        verify(accountRepository, times(1)).save(newAccount);
    }

    @Test
    void createAccount_ShouldThrowException_WhenUnexpectedErrorOccurs() {
        Account newAccount = Account.builder()
                .firstName("Dave")
                .lastName("Smith")
                .email("test123@example.com")
                .passwordHash("password")
                .role(Account.Role.CHILD)
                .build();

        when(accountRepository.findByEmail(newAccount.getEmail())).thenReturn(Optional.empty());
        when(accountRepository.save(newAccount)).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.createAccount(newAccount);
        });

        assertTrue(exception.getMessage().contains("Failed to create account"));
        verify(accountRepository, times(1)).findByEmail(newAccount.getEmail());
        verify(accountRepository, times(1)).save(newAccount);
    }


    private final Account validAccount = Account.builder()
            .userId(USER_ID)
            .firstName("John")
            .lastName("Smith")
            .email("test@example.com")
            .passwordHash("password")
            .role(Account.Role.PARENT)
            .build();

    private final Account validCreateAccount = Account.builder()
            .firstName("Dave")
            .lastName("Smith")
            .email("test123@example.com")
            .passwordHash("password")
            .role(Account.Role.CHILD)
            .build();
}
