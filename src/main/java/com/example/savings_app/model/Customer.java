package com.example.savings_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "Customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment for user_id
    @Column(name = "cust_id", nullable = false)
    private Integer custId;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "child_id")
    private Integer childId;

    public Customer() {

    }
}
