package com.ispan.bankmanagement.account.enums;

/**
 * 交易紀錄類型列舉
 * 統一使用 Enum 來管理交易類型，避免開發者在程式碼裡手打 String 造成錯字，
 * 也能限制 API 只接受合法定義的交易類型。
 */
public enum TransLogType {
    DEPOSIT,       // 存款
    WITHDRAW,      // 提款
    TRANSFER_OUT,  // 轉出 (扣款方)
    TRANSFER_IN,   // 轉入 (收款方)
    INTEREST,      // 結息 (系統發放利息)
    FEE            // 手續費 (例如跨行轉帳、提款的手續費)
}