package com.ispan.bankmanagement.account.common.exception;

/**
 * 自訂例外類別，用於表示請求的資源 (例如特定帳戶) 不存在。
 * 繼承自 RuntimeException，使其成為一個 Unchecked Exception。
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
