package com.ispan.bankmanagement.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name="account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue
    private String Account;

    @NonNull
    private Integer customerId;

    @NonNull
    private String type;

    @NonNull
    private String currency;

    @NonNull
    private BigDecimal balance;

    @NonNull
    private java.util.Date createAt;

    @NonNull
    private java.util.Date changeAt;
}
