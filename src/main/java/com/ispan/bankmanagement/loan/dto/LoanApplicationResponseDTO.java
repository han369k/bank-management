package com.ispan.bankmanagement.loan.dto;

import com.ispan.bankmanagement.loan.LoanApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplicationResponseDTO {

    private String applicationId;
    private Integer customerId;
    private String applyType;
    private Long applyAmount;
    private Integer applyPeriod;
    private BigDecimal rate;                  // 申請當下利率

    private LoanApplicationStatus status;

    private Long approvedAmount;              // 審核後才有值
    private Integer approvedPeriod;
    private BigDecimal approvedRate;

    private Integer reviewerId;
    private LocalDateTime createTime;
    private LocalDateTime reviewTime;

    // Getters & Setters
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getApplyType() { return applyType; }
    public void setApplyType(String applyType) { this.applyType = applyType; }

    public Long getApplyAmount() { return applyAmount; }
    public void setApplyAmount(Long applyAmount) { this.applyAmount = applyAmount; }

    public Integer getApplyPeriod() { return applyPeriod; }
    public void setApplyPeriod(Integer applyPeriod) { this.applyPeriod = applyPeriod; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public LoanApplicationStatus getStatus() { return status; }
    public void setStatus(LoanApplicationStatus status) { this.status = status; }

    public Long getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(Long approvedAmount) { this.approvedAmount = approvedAmount; }

    public Integer getApprovedPeriod() { return approvedPeriod; }
    public void setApprovedPeriod(Integer approvedPeriod) { this.approvedPeriod = approvedPeriod; }

    public BigDecimal getApprovedRate() { return approvedRate; }
    public void setApprovedRate(BigDecimal approvedRate) { this.approvedRate = approvedRate; }

    public Integer getReviewerId() { return reviewerId; }
    public void setReviewerId(Integer reviewerId) { this.reviewerId = reviewerId; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getReviewTime() { return reviewTime; }
    public void setReviewTime(LocalDateTime reviewTime) { this.reviewTime = reviewTime; }
}