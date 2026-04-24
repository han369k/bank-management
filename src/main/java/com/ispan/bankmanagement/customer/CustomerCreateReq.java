package com.ispan.bankmanagement.customer;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增顧客 DTO
 */
@Data
public class CustomerCreateReq {

    @NotBlank(message = "身分證字號不可為空白")
    @Size(min = 10, max = 10, message = "身分證字號長度錯誤")
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

    // 開戶預設值
    private BigDecimal income;
    private Integer creditScore;
}