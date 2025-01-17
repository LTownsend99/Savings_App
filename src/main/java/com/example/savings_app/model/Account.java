package com.example.savings_app.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "Account")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment for user_id
  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  @Column(name = "child_id")
  private Integer childId;

  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt;

  @Column(name = "dob", nullable = false)
  private LocalDate dob;

  public enum Role {
    child,
    parent
  }

  public Account() {}
}
