package com.ispan.bankmanagement.customer;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * 顧客 Entity
 */
public class Customer {

    private Integer customerId;      // customer_id (PK)
    private String idNumber;         // 身分證字號
    private String name;             // 姓名
    private Date dateOfBirth;        // 生日
    private String nationality;      // 國籍
    private String address;          // 地址
    private String phone;            // 電話
    private String email;            // Email
    private String passwordHash;     // 密碼(Hash)
    private Integer failedLoginAttempts; // 登入失敗次數
    private BigDecimal income;       // 月收入（銀行金額一律用 BigDecimal，不用 double！）
    private Integer creditScore;         // 信用分數
    private Timestamp createdAt;     // 建立時間
    private Timestamp updatedAt;     // 最後更新時間
    private String status;           // 狀態：Active / VIP / Frozen / Blacklist

    // Getters & Setters
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
