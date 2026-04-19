package com.ispan.bankmanagement.loan;

public enum LoanApplicationStatus {
    PENDING,            // 待審核
    PENDING_CONFIRM,    // 待客戶確認（銀行送方案）
    APPROVED,           // 已核准（銀行直接核准）
    CONFIRMED,          // 已確認（客戶接受銀行方案）
    REJECTED            // 已拒絕
}

