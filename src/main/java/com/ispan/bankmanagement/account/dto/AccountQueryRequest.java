package com.ispan.bankmanagement.account.dto;

import com.ispan.bankmanagement.account.enums.AccountStatus;
import com.ispan.bankmanagement.account.enums.AccountType;

public record AccountQueryRequest(
        AccountType type,
        AccountStatus status,
        String accountNumber
) {}