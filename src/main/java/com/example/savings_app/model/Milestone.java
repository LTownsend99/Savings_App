package com.example.savings_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "Milestone")
public class Milestone {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment for user_id
  @Column(name = "milestone_id", nullable = false)
  private Integer milestoneId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private Account user;

  @Column(name = "milestone_name", nullable = false)
  private String milestoneName;

  @Column(name = "target_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal targetAmount;

  @Column(name = "saved_amount", precision = 10, scale = 2)
  private BigDecimal savedAmount = BigDecimal.ZERO;

  @Column(name = "start_date", nullable = false)
  @Temporal(TemporalType.DATE)
  private LocalDate startDate;

  @Column(name = "completion_date")
  @Temporal(TemporalType.DATE)
  private LocalDate completionDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private Status status;

  public enum Status {
    active,
    completed
  }

  public Milestone() {}
}
