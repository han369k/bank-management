package com.ispan.bankmanagement.account.exception;

/**
 * 當嘗試新增一個已存在的帳戶時拋出的例外。
 * 使用此特定例外，可以讓上層（如全局例外處理器）精準捕捉並回傳語意化的錯誤響應，
 * 例如 HTTP 409 Conflict。
 */
public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String message) {
        super(message);
    }
}