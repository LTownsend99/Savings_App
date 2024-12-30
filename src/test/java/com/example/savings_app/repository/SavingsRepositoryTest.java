package com.example.savings_app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Savings;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
public class SavingsRepositoryTest {

  @Autowired private SavingsRepository savingsRepository;

  private Savings savings1;
  private Savings savings2;

  @BeforeEach
  public void setUp() {
    // Setup code to create Savings instances for testing
    savings1 =
        Savings.builder()
            .user(Account.builder().userId(1).build()) // Assume Account entity is set up correctly
            .amount(new BigDecimal("500.00"))
            .date(LocalDate.of(2024, 1, 1))
            .milestoneId(1)
            .build();

    savings2 =
        Savings.builder()
            .user(Account.builder().userId(2).build()) // Assume Account entity is set up correctly
            .amount(new BigDecimal("1500.00"))
            .date(LocalDate.of(2024, 2, 1))
            .milestoneId(2)
            .build();

    // Save the savings records to the repository
    savingsRepository.save(savings1);
    savingsRepository.save(savings2);
  }

  @Test
  public void testFindByIdFound() {
    Optional<Savings> foundSavings = savingsRepository.findById(savings1.getSavingsId());
    assertThat(foundSavings).isPresent();
    assertThat(foundSavings.get().getSavingsId()).isEqualTo(savings1.getSavingsId());
  }

  @Test
  public void testFindByIdNotFound() {
    Optional<Savings> foundSavings =
        savingsRepository.findById(9999); // Assuming this ID doesn't exist
    assertThat(foundSavings).isNotPresent();
  }

  @Test
  public void testFindByDateFound() {
    List<Savings> foundSavings = savingsRepository.findByDate(LocalDate.of(2024, 1, 1));
    assertThat(foundSavings).hasSize(1);
    assertThat(foundSavings.get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 1));
  }

  @Test
  public void testFindByDateNotFound() {
    List<Savings> foundSavings = savingsRepository.findByDate(LocalDate.of(2025, 1, 1));
    assertThat(foundSavings).isEmpty();
  }

  @Test
  public void testFindByMilestoneIdFound() {
    Optional<Savings> foundSavings = savingsRepository.findByMilestoneId(1);
    assertThat(foundSavings).isPresent();
    assertThat(foundSavings.get().getMilestoneId()).isEqualTo(1);
  }

  @Test
  public void testFindByMilestoneIdNotFound() {
    Optional<Savings> foundSavings = savingsRepository.findByMilestoneId(999);
    assertThat(foundSavings).isNotPresent();
  }

  @Test
  public void testDeleteByIdFound() {
    Integer id = savings1.getSavingsId();
    savingsRepository.deleteById(id);

    Optional<Savings> deletedSavings = savingsRepository.findById(id);
    assertThat(deletedSavings).isNotPresent();
  }

  @Test
  public void testDeleteByIdNotFound() {
    Integer nonExistentId = 9999; // Assuming this ID doesn't exist
    savingsRepository.deleteById(nonExistentId);

    // Try to find the non-existent savings record
    Optional<Savings> deletedSavings = savingsRepository.findById(nonExistentId);
    assertThat(deletedSavings).isNotPresent();
  }
}
