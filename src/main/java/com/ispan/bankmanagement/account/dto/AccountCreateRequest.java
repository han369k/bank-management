package com.ispan.bankmanagement.account.dto;

import com.ispan.bankmanagement.account.enums.AccountCurrency;
import com.ispan.bankmanagement.account.enums.AccountType;

import java.math.BigDecimal;

public record AccountCreateRequest(
        Integer customerId,
        AccountType accountType,
        AccountCurrency accountCurrency,
        BigDecimal balance
) {}
