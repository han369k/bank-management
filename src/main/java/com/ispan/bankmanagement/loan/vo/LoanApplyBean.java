package com.ispan.bankmanagement.loan.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class LoanApplyBean {

    private String applicationId;
    private String customerId;

    private BigDecimal applyAmount;
    private Integer applyPeriod;
    private Timestamp createTime;

    private BigDecimal approvedAmount;
    private BigDecimal approvedRate;
    private Integer approvedPeriod;

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

    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
    }

    public Integer getApplyPeriod() {
        return applyPeriod;
    }

    public void setApplyPeriod(Integer applyPeriod) {
        this.applyPeriod = applyPeriod;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getApprovedRate() {
        return approvedRate;
    }

    public void setApprovedRate(BigDecimal approvedRate) {
        this.approvedRate = approvedRate;
    }

    public Integer getApprovedPeriod() {
        return approvedPeriod;
    }

    public void setApprovedPeriod(Integer approvedPeriod) {
        this.approvedPeriod = approvedPeriod;
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