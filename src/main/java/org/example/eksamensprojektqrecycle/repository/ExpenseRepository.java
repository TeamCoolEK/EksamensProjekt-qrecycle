package org.example.eksamensprojektqrecycle.repository;

import org.example.eksamensprojektqrecycle.model.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
}