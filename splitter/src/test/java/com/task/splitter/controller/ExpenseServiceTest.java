package com.task.splitter.service;

import com.task.splitter.dto.ExpenseRequest;
import com.task.splitter.model.Expense;
import com.task.splitter.model.ExpenseParticipant;
import com.task.splitter.model.Role;
import com.task.splitter.model.User;
import com.task.splitter.repository.ExpenseParticipantRepository;
import com.task.splitter.repository.ExpenseRepository;
import com.task.splitter.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseParticipantRepository expenseParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void createExpense_shouldSplitAmountEquallyBetweenParticipants() {
        User ana = User.builder()
                .id(1L)
                .username("ana")
                .email("ana@test.com")
                .password("pass")
                .role(Role.USER)
                .build();

        User maria = User.builder()
                .id(2L)
                .username("maria")
                .email("maria@test.com")
                .password("pass")
                .role(Role.USER)
                .build();

        ExpenseRequest request = new ExpenseRequest(
                "Pizza",
                new BigDecimal("100.00"),
                1L,
                List.of(1L, 2L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(ana, maria));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expense expense = expenseService.createExpense(request);

        for (ExpenseParticipant participant : expense.getParticipants()) {
            assertEquals(new BigDecimal("50.00"), participant.getAmountOwed());
        }
    }

    @Test
    void createExpense_shouldCreateParticipantForEachUser() {
        User ana = User.builder()
                .id(1L)
                .username("ana")
                .email("ana@test.com")
                .password("pass")
                .role(Role.USER)
                .build();

        User maria = User.builder()
                .id(2L)
                .username("maria")
                .email("maria@test.com")
                .password("pass")
                .role(Role.USER)
                .build();

        User ion = User.builder()
                .id(3L)
                .username("ion")
                .email("ion@test.com")
                .password("pass")
                .role(Role.USER)
                .build();

        ExpenseRequest request = new ExpenseRequest(
                "Sushi",
                new BigDecimal("90.00"),
                1L,
                List.of(1L, 2L, 3L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(userRepository.findAllById(List.of(1L, 2L, 3L))).thenReturn(List.of(ana, maria, ion));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expense expense = expenseService.createExpense(request);

        assertEquals(3, expense.getParticipants().size());
    }

    @Test
    void createExpense_shouldThrowExceptionWhenParticipantsListIsEmpty() {
        User ana = User.builder()
                .id(1L)
                .username("ana")
                .email("ana@test.com")
                .password("pass")
                .role(Role.USER)
                .build();

        ExpenseRequest request = new ExpenseRequest(
                "Coffee",
                new BigDecimal("30.00"),
                1L,
                List.of()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(userRepository.findAllById(List.of())).thenReturn(List.of());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> expenseService.createExpense(request)
        );

        assertEquals("Expense must have at least one participant", exception.getMessage());
    }

}