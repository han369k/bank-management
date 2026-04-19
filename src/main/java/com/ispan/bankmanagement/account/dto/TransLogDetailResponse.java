package com.ispan.bankmanagement.account.dto;

import com.ispan.bankmanagement.account.enums.TransLogType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransLogDetailResponse(
        String referenceId,
        BigDecimal amount,
        TransLogType type,
        String operationAccount,
        String otherAccount,
        BigDecimal balance,
        LocalDateTime transactionTime,
        String note
) {}