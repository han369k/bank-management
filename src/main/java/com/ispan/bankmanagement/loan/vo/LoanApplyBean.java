package com.ispan.bankmanagement.loan.vo;

import java.math.*;
import java.sql.Timestamp;

public class LoanApplyBean {

    private String applicationId;
    private String customerId;

    private String applyType;
    private Long applyAmount;
    private Integer applyPeriod;
    private BigDecimal rate;
    private Timestamp createTime;

    private Long approvedAmount;
    private Integer approvedPeriod;
    private BigDecimal approvedRate;

    private String status;

    private Integer reviewerId;
    private Timestamp reviewTime;

    // ===== Getter / Setter =====

    public String getApplicationId() {
        return applicationId;
    }
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getApplyType() {
        return applyType;
    }
    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public Long getApplyAmount() {
        return applyAmount;
    }
    public void setApplyAmount(Long applyAmount) {
        this.applyAmount = applyAmount;
    }

    public Integer getApplyPeriod() {
        return applyPeriod;
    }
    public void setApplyPeriod(Integer applyPeriod) {
        this.applyPeriod = applyPeriod;
    }

    public BigDecimal getRate() {
        return rate;
    }
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getApprovedAmount() {
        return approvedAmount;
    }
    public void setApprovedAmount(Long approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Integer getApprovedPeriod() {
        return approvedPeriod;
    }
    public void setApprovedPeriod(Integer approvedPeriod) {
        this.approvedPeriod = approvedPeriod;
    }

    public BigDecimal getApprovedRate() {
        return approvedRate;
    }
    public void setApprovedRate(BigDecimal approvedRate) {
        this.approvedRate = approvedRate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getReviewerId() {
        return reviewerId;
    }
    public void setReviewerId(Integer reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Timestamp getReviewTime() {
        return reviewTime;
    }
    public void setReviewTime(Timestamp reviewTime) {
        this.reviewTime = reviewTime;
    }
}