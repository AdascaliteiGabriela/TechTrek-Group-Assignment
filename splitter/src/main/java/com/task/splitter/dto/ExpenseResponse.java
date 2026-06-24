package com.task.splitter.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseResponse(
        Long id,
        String description,
        BigDecimal totalAmount,
        String paidByUsername,
        LocalDateTime createdAt,
        List<ParticipantResponse> participants
) {
}