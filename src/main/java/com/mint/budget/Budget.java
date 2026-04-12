package com.mint.budget;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets", uniqueConstraints = @UniqueConstraint(columnNames = "category"))
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String category;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal monthlyLimit;
}
