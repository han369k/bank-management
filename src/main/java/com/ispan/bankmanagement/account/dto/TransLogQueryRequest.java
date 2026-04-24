package com.ispan.bankmanagement.account.dto;

import java.time.LocalDate;

public record TransLogQueryRequest(
        Integer customerId,
        String account,
        LocalDate startDate,
        LocalDate endDate,
        Integer page,
        Integer size
) {
    public TransLogQueryRequest {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;
    }
}