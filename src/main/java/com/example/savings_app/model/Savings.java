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
@Table(name = "Savings")
public class Savings {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment for user_id
  @Column(name = "sav_id", nullable = false)
  private Integer savingsId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private Account user;

  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(name = "date", nullable = false)
  @Temporal(TemporalType.DATE)
  private LocalDate date;

  @Column(name = "milestone_id", nullable = false)
  private int milestoneId;

  public Savings() {}
}
