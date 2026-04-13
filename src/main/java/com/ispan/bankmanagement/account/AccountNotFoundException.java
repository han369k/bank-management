package com.ispan.bankmanagement.account;

/**
 * 當業務邏輯預期要找到一個帳戶，但根據提供的條件在資料庫中找不到時，所拋出的例外。
 *
 * 建立一個獨立的例外類別（而不是使用通用的 RuntimeException），主要有以下優點：
 * 1. 語意清晰：類別名稱本身就清楚說明了錯誤的性質。
 * 2. 精準捕捉：上層呼叫者 (如 Service 或 Controller) 可以針對此特定例外進行捕捉，並做出相應的處理（例如：回傳 HTTP 404 Not Found）。
 * 3. 職責分離：讓業務邏輯的錯誤與系統底層的錯誤（如 DataAccessException）分離，使架構更清晰。
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}