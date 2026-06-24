package com.task.splitter.dto;
import java.math.BigDecimal;
import java.util.List;
public record ExpenseRequest(
        String description,
        BigDecimal totalAmount,
        Long paidById,
        List<Long> participantIds
) {
}