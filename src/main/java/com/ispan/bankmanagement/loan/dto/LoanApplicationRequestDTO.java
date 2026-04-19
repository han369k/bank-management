package com.ispan.bankmanagement.loan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LoanApplicationRequestDTO {

    @NotNull(message = "顧客代號不可為空")
    private Integer customerId;

    @NotBlank(message = "貸款類型不可為空")
    private String applyType;

    @NotNull(message = "申請金額不可為空")
    @Positive(message = "申請金額須大於 0")
    private Long applyAmount;

    @NotNull(message = "申請期數不可為空")
    @Min(value = 1, message = "申請期數最少 1 個月")
    private Integer applyPeriod;

    // Getters & Setters
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getApplyType() { return applyType; }
    public void setApplyType(String applyType) { this.applyType = applyType; }

    public Long getApplyAmount() { return applyAmount; }
    public void setApplyAmount(Long applyAmount) { this.applyAmount = applyAmount; }

    public Integer getApplyPeriod() { return applyPeriod; }
    public void setApplyPeriod(Integer applyPeriod) { this.applyPeriod = applyPeriod; }
}