package com.task.splitter.controller;

import com.task.splitter.dto.ExpenseRequest;
import com.task.splitter.dto.ExpenseResponse;
import com.task.splitter.dto.ParticipantResponse;
import com.task.splitter.model.Expense;
import com.task.splitter.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ExpenseResponse create(@Valid @RequestBody ExpenseRequest request) {
        Expense expense = expenseService.createExpense(request);
        return mapToResponse(expense);
    }

    @GetMapping("/my")
    public List<ExpenseResponse> getMyExpenses(@AuthenticationPrincipal UserDetails userDetails) {
        return expenseService.getUserExpensesByEmail(userDetails.getUsername())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @GetMapping("/balance")
    public Map<String, BigDecimal> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return expenseService.calculateBalancesByEmail(userDetails.getUsername());
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getTotalAmount(),
                expense.getPaidBy().getUsername(),
                expense.getCreatedAt(),
                expense.getParticipants()
                        .stream()
                        .map(participant -> new ParticipantResponse(
                                participant.getUser().getId(),
                                participant.getUser().getUsername(),
                                participant.getAmountOwed()
                        ))
                        .toList()
        );
    }


    @GetMapping("/all")
    public List<ExpenseResponse> getAllExpenses() {
        return expenseService.getAllExpenses()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}