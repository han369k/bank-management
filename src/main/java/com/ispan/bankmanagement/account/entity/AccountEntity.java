package com.ispan.bankmanagement.account.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountEntity {
    private String account;
    private String customerId;
    private String type;
    private String currency;
    private BigDecimal balance;
    private String status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime changeAt;

    public AccountEntity() {
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getChangeAt() {
        return changeAt;
    }

    public void setChangeAt(LocalDateTime changeAt) {
        this.changeAt = changeAt;
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "account='" + account + '\'' +
                ", customerId='" + customerId + '\'' +
                ", type='" + type + '\'' +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", createAt=" + createAt +
                ", changeAt=" + changeAt +
                '}';
    }
}
