package com.example.splitter.controller;

import com.example.splitter.model.Expense;
import com.example.splitter.model.User;
import com.example.splitter.service.ExpenseService;
import com.example.splitter.dto.ExpenseRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public Expense create(@RequestBody ExpenseRequest request) {
        return expenseService.createExpense(request);
    }

    @GetMapping("/my")
    public List<Expense> getMyExpenses(@AuthenticationPrincipal User user) {
        return expenseService.getUserExpenses(user);
    }

    @GetMapping("/balance")
    public Map<String, Double> getBalance(@AuthenticationPrincipal User user) {
        return expenseService.calculateBalances(user);
    }
}