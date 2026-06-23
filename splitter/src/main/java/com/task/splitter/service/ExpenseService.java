package com.example.splitter.service;

import com.example.splitter.model.Expense;
import com.example.splitter.model.User;
import com.example.splitter.repository.ExpenseRepository;
import com.example.splitter.repository.UserRepository;
import com.example.splitter.dto.ExpenseRequest;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public Expense createExpense(ExpenseRequest request) {

        User paidBy = userRepository.findById(request.paidById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> participants = userRepository.findAllById(request.participantIds);

        Expense expense = new Expense();
        expense.setDescription(request.description);
        expense.setTotalAmount(request.totalAmount);
        expense.setPaidBy(paidBy);
        expense.setParticipants(participants);

        return expenseRepository.save(expense);

    public Map<String, Double> calculateSplit(Expense expense) {

        Map<String, Double> result = new HashMap<>();

        int numberOfParticipants = expense.getParticipants().size();
        double share = expense.getTotalAmount() / numberOfParticipants;

        for (User user : expense.getParticipants()) {
            if (!user.getId().equals(expense.getPaidBy().getId())) {
                result.put(user.getUsername(), share);
            }
        }

        return result;
    }

    public List<Expense> getUserExpenses(User user) {
        return expenseRepository.findByParticipantsContaining(user);
    }

    public Map<String, Double> calculateBalances(User user) {

        List<Expense> expenses = expenseRepository.findByParticipantsContaining(user);

        Map<String, Double> balances = new HashMap<>();

        for (Expense expense : expenses) {

            double share = expense.getTotalAmount() / expense.getParticipants().size();

            for (User participant : expense.getParticipants()) {

                if (participant.getId().equals(user.getId())) continue;

                if (expense.getPaidBy().getId().equals(user.getId())) {
                    // others owe YOU
                    balances.put(participant.getUsername(),
                            balances.getOrDefault(participant.getUsername(), 0.0) + share);
                }

                if (participant.getId().equals(user.getId())
                        && !expense.getPaidBy().getId().equals(user.getId())) {

                    String paidByName = expense.getPaidBy().getUsername();

                    balances.put(paidByName,
                            balances.getOrDefault(paidByName, 0.0) - share);
                }
            }
        }

        return balances;
    }
}
`