package com.ispan.bankmanagement.account.dto;

import com.ispan.bankmanagement.account.enums.AccountStatus;

public record AccountUpdateRequest (
        AccountStatus accountStatus
){}
