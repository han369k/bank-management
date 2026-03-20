package com.ispan.bankmanagement.account.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransLog {

    private Long transLogId;
    private String referenceId;
    private BigDecimal amount;
    private String type;
    private String operationAccount;
    private String otherAccount;
    private BigDecimal balance;
    private LocalDateTime transactionTime;
    private String note;

    public TransLog() {
    }

    public Long getTransLogId() {
        return transLogId;
    }

    public void setTransLogId(Long transLogId) {
        this.transLogId = transLogId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperationAccount() {
        return operationAccount;
    }

    public void setOperationAccount(String operationAccount) {
        this.operationAccount = operationAccount;
    }

    public String getOtherAccount() {
        return otherAccount;
    }

    public void setOtherAccount(String otherAccount) {
        this.otherAccount = otherAccount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "TransLog{" +
                "transLogId=" + transLogId +
                ", referenceId='" + referenceId + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", operationAccount='" + operationAccount + '\'' +
                ", otherAccount='" + otherAccount + '\'' +
                ", balance=" + balance +
                ", transactionTime=" + transactionTime +
                ", note='" + note + '\'' +
                '}';
    }
}