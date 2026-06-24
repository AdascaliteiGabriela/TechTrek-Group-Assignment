package com.task.splitter.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {
}