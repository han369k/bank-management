package com.ispan.bankmanagement.account.dto;

import com.ispan.bankmanagement.account.enums.AccountCurrency;
import com.ispan.bankmanagement.account.enums.AccountStatus;
import com.ispan.bankmanagement.account.enums.AccountType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AccountDetailResponse (
        String accountNumber,
        Integer customerId,
        AccountType type,
        AccountCurrency currency,
        BigDecimal balance,
        AccountStatus status,
        OffsetDateTime createAt,
        OffsetDateTime changeAt
){}
