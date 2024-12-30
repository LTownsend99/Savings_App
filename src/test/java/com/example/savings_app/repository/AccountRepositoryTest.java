package com.example.savings_app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.savings_app.model.Account;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

  @Autowired private AccountRepository accountRepository;

  private Account testAccount;

  private static final LocalDate NOW = LocalDate.parse("2024-11-16");

  @BeforeEach
  void setUp() {
    // Initialize a test account
    testAccount =
        Account.builder()
            .userId(1)
            .firstName("John")
            .lastName("Smith")
            .email("test@example.com")
            .passwordHash("password")
            .createdAt(NOW)
            .role(Account.Role.parent)
            .dob(LocalDate.parse("1990-11-16"))
            .build();

    // Save the test account to ensure it exists in the DB
    accountRepository.save(testAccount);
  }

  @Test
  void testFindById() {

    // Attempt to retrieve the account by its ID
    Optional<Account> retrievedAccount = accountRepository.findById(testAccount.getUserId());

    // Validate the retrieved account matches the saved account
    assertThat(retrievedAccount).isPresent();
    assertThat(retrievedAccount.get().getUserId()).isEqualTo(testAccount.getUserId());
    assertThat(retrievedAccount.get().getEmail()).isEqualTo(testAccount.getEmail());
  }

  @Test
  void testFindById_NotFound() {
    // Trying to retrieve an Account with an ID that doesn't exist
    Optional<Account> retrievedAccount =
        accountRepository.findById(999); // Assuming 999 doesn't exist in DB

    // Assert that no account is returned (Optional should be empty)
    assertThat(retrievedAccount).isNotPresent();
  }

  @Test
  void testDeleteAccount() {
    // Delete the account by its ID
    accountRepository.deleteById(testAccount.getUserId());

    // Validate the account no longer exists
    Optional<Account> deletedAccount = accountRepository.findById(testAccount.getUserId());
    assertThat(deletedAccount).isNotPresent();
  }

  @Test
  void testDeleteAccount_NotFound() {
    // Delete an Account that doesn't exist (ID = 999)
    accountRepository.deleteById(999); // Assuming 999 does not exist

    // No exception should be thrown, and the repository should not throw any error
    // We just verify that no Account exists with ID 999
    Optional<Account> customerAfterDelete = accountRepository.findById(999);
    assertThat(customerAfterDelete).isNotPresent(); // Account should not exist
  }

  @Test
  void testFindByEmail_Found() {
    // Test that the account can be retrieved by email
    Optional<Account> retrievedAccount = accountRepository.findByEmail("test@example.com");

    // Assert that the account is present and its email matches the one used for lookup
    assertThat(retrievedAccount).isPresent();
    assertThat(retrievedAccount.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void testFindByEmail_NotFound() {
    // Test that no account is found with an email that doesn't exist
    Optional<Account> retrievedAccount =
        accountRepository.findByEmail("non.existing.email@example.com");

    // Assert that no account is found (Optional should be empty)
    assertThat(retrievedAccount).isNotPresent();
  }
}
