package com.example.savings_app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Milestone;
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
public class MilestoneRepositoryTest {

  @Autowired private MilestoneRepository milestoneRepository;

  private Milestone milestone;

  private static final LocalDate NOW = LocalDate.parse("2024-11-16");

  @BeforeEach
  public void setUp() {
    // Setup code for creating a milestone object
    milestone =
        Milestone.builder()
            .milestoneName("Test Milestone")
            .user(Account.builder().userId(1).build())
            .targetAmount(new BigDecimal("1000.00"))
            .savedAmount(new BigDecimal("200.00"))
            .startDate(NOW)
            .completionDate(NOW.plusDays(10))
            .status(Milestone.Status.active)
            .build();

    // Save the milestone to the repository
    milestoneRepository.save(milestone);
  }

  @Test
  public void testFindByIdFound() {
    Optional<Milestone> foundMilestone = milestoneRepository.findById(milestone.getMilestoneId());
    assertThat(foundMilestone).isPresent();
    assertThat(foundMilestone.get().getMilestoneId()).isEqualTo(milestone.getMilestoneId());
  }

  @Test
  public void testFindByIdNotFound() {
    Integer nonExistentId = 9999; // Assuming this ID doesn't exist in the DB
    Optional<Milestone> foundMilestone = milestoneRepository.findById(nonExistentId);
    assertThat(foundMilestone).isNotPresent();
  }

  @Test
  public void testFindByMilestoneNameFound() {
    Optional<Milestone> foundMilestone = milestoneRepository.findByMilestoneName("Test Milestone");
    assertThat(foundMilestone).isPresent();
    assertThat(foundMilestone.get().getMilestoneName()).isEqualTo("Test Milestone");
  }

  @Test
  public void testFindByMilestoneNameNotFound() {
    Optional<Milestone> foundMilestone =
        milestoneRepository.findByMilestoneName("Nonexistent Milestone");
    assertThat(foundMilestone).isNotPresent();
  }

  @Test
  public void testFindByStartDateFound() {
    List<Milestone> milestones = milestoneRepository.findByStartDate(NOW);
    assertThat(milestones).hasSize(1);
    assertThat(milestones.get(0).getStartDate()).isEqualTo(NOW);
  }

  @Test
  public void testFindByStartDateNotFound() {
    List<Milestone> milestones = milestoneRepository.findByStartDate(LocalDate.of(2025, 1, 1));
    assertThat(milestones).isEmpty();
  }

  @Test
  public void testFindByCompletionDateFound() {
    List<Milestone> milestones = milestoneRepository.findByCompletionDate(NOW.plusDays(10));
    assertThat(milestones).hasSize(1);
    assertThat(milestones.get(0).getCompletionDate()).isEqualTo(NOW.plusDays(10));
  }

  @Test
  public void testFindByCompletionDateNotFound() {
    List<Milestone> milestones =
        milestoneRepository.findByCompletionDate(LocalDate.of(2025, 12, 31));
    assertThat(milestones).isEmpty();
  }

  @Test
  public void testDeleteByIdFound() {
    Integer id = milestone.getMilestoneId();
    milestoneRepository.deleteById(id);

    Optional<Milestone> deletedMilestone = milestoneRepository.findById(id);
    assertThat(deletedMilestone).isNotPresent();
  }

  @Test
  public void testDeleteByIdNotFound() {
    Integer nonExistentId = 9999; // Assuming this ID doesn't exist in the DB
    milestoneRepository.deleteById(nonExistentId);

    // We attempt to find the non-existent milestone (it should still not exist)
    Optional<Milestone> deletedMilestone = milestoneRepository.findById(nonExistentId);
    assertThat(deletedMilestone).isNotPresent();
  }
}
