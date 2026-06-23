package com.task.splitter.dto;
import java.util.List;
public class ExpenseRequest {
    public String description;
    public double totalAmount;
    public Long paidById;
    public List<Long> participantIds;
}