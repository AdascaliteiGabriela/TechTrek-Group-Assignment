package com.task.splitter.dto;

import java.math.BigDecimal;

public record ParticipantResponse(
        Long userId,
        String username,
        BigDecimal amountOwed
) {
}