package com.ispan.bankmanagement.account.dto;

import java.math.BigDecimal;

public record AccountAmountRequest(
        BigDecimal amount
) {}