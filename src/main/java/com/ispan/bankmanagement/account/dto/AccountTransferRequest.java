package com.ispan.bankmanagement.account.dto;

import java.math.BigDecimal;

public record AccountTransferRequest(
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        String note
) {}