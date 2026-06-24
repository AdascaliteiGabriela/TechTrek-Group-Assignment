package com.task.splitter.service;

import com.task.splitter.dto.ExpenseRequest;
import com.task.splitter.model.Expense;
import com.task.splitter.model.ExpenseParticipant;
import com.task.splitter.model.User;
import com.task.splitter.repository.ExpenseParticipantRepository;
import com.task.splitter.repository.ExpenseRepository;
import com.task.splitter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository expenseParticipantRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseParticipantRepository expenseParticipantRepository,
            UserRepository userRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseParticipantRepository = expenseParticipantRepository;
        this.userRepository = userRepository;
    }

    public Expense createExpense(ExpenseRequest request) {

        User paidBy = userRepository.findById(request.paidById())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> participants = userRepository.findAllById(request.participantIds());

        if (participants.isEmpty()) {
            throw new RuntimeException("Expense must have at least one participant");
        }

        if (participants.size() != request.participantIds().size()) {
            throw new RuntimeException("One or more participants were not found");
        }

        BigDecimal amountOwed = request.totalAmount()
                .divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.HALF_UP);

        Expense expense = new Expense();
        expense.setDescription(request.description());
        expense.setTotalAmount(request.totalAmount());
        expense.setPaidBy(paidBy);
        expense.setCreatedAt(LocalDateTime.now());

        for (User participant : participants) {
            ExpenseParticipant expenseParticipant = new ExpenseParticipant();
            expenseParticipant.setExpense(expense);
            expenseParticipant.setUser(participant);
            expenseParticipant.setAmountOwed(amountOwed);

            expense.getParticipants().add(expenseParticipant);
        }

        return expenseRepository.save(expense);
    }

    public Map<String, BigDecimal> calculateSplit(Expense expense) {

        Map<String, BigDecimal> result = new HashMap<>();

        for (ExpenseParticipant participant : expense.getParticipants()) {
            User user = participant.getUser();

            if (!user.getId().equals(expense.getPaidBy().getId())) {
                result.put(user.getUsername(), participant.getAmountOwed());
            }
        }

        return result;
    }

    public List<Expense> getUserExpenses(User user) {

        List<ExpenseParticipant> participations = expenseParticipantRepository.findByUser(user);

        Set<Expense> expenses = new LinkedHashSet<>();

        for (ExpenseParticipant participation : participations) {
            expenses.add(participation.getExpense());
        }

        expenses.addAll(expenseRepository.findByPaidBy(user));

        return new ArrayList<>(expenses);
    }

    public Map<String, BigDecimal> calculateBalances(User user) {

        List<Expense> expenses = getUserExpenses(user);

        Map<String, BigDecimal> balances = new HashMap<>();

        for (Expense expense : expenses) {

            User paidBy = expense.getPaidBy();

            if (paidBy.getId().equals(user.getId())) {
                for (ExpenseParticipant participant : expense.getParticipants()) {

                    User participantUser = participant.getUser();

                    if (!participantUser.getId().equals(user.getId())) {
                        balances.put(
                                participantUser.getUsername(),
                                balances.getOrDefault(participantUser.getUsername(), BigDecimal.ZERO)
                                        .add(participant.getAmountOwed())
                        );
                    }
                }
            } else {
                for (ExpenseParticipant participant : expense.getParticipants()) {

                    User participantUser = participant.getUser();

                    if (participantUser.getId().equals(user.getId())) {
                        balances.put(
                                paidBy.getUsername(),
                                balances.getOrDefault(paidBy.getUsername(), BigDecimal.ZERO)
                                        .subtract(participant.getAmountOwed())
                        );
                    }
                }
            }
        }

        return balances;
    }

    public List<Expense> getUserExpensesByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getUserExpenses(user);
    }

    public Map<String, BigDecimal> calculateBalancesByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return calculateBalances(user);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }
}