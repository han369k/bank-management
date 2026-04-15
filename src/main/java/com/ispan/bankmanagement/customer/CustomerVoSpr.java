package com.ispan.bankmanagement.customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VO (Value Object) — 顧客資料容器
 * 【Spring 改寫版】引入 Lombok、現代化 Date API 與資料驗證標籤
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVoSpr { // 📍 類別名稱已更新為 CustomerVoSpr

    private String customerId;

    @NotBlank(message = "身分證字號不可為空白")
    @Size(min = 10, max = 10, message = "身分證字號必須為 10 碼")
    private String idNumber;         

    @NotBlank(message = "姓名不可為空")
    private String name;             

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String nationality;      
    private String address;          
    private String phone;            

    @Email(message = "Email 格式不正確")
    private String email;            

    private String passwordHash;     
    private int failedLoginAttempts; 
    
    private BigDecimal income;
    private int creditScore;         

    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt; 

    private String status;
}