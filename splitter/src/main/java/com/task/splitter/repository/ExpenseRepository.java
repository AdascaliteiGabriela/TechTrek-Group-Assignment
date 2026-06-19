package com.task.splitter.repository;

import com.task.splitter.model.Expense;
import com.task.splitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByPaidBy(User user);
}