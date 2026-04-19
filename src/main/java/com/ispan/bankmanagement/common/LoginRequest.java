package com.ispan.bankmanagement.common;

public record LoginRequest(
        String username,
        String password
) {}