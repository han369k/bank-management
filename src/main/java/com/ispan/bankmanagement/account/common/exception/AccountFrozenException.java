package com.ispan.bankmanagement.account.common.exception;

public class AccountFrozenException extends RuntimeException {
  public AccountFrozenException(String message) {
    super(message);
  }
}