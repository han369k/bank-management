package com.ispan.bankmanagement.loan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LoanReviewRequestDTO {

    @NotNull(message = "審核行員代號不可為空")
    private Integer reviewerId;

    @NotNull(message = "核准金額不可為空")
    @Positive(message = "核准金額須大於 0")
    private Long approvedAmount;

    @NotNull(message = "核准期數不可為空")
    @Min(value = 1, message = "核准期數最少 1 個月")
    private Integer approvedPeriod;

    // ⛔ 不含 approvedRate（後端 calculateRate() 決定）
    // ⛔ 不含 applyType（Service 從 DB 查原申請取得）

    // Getters & Setters
    public Integer getReviewerId() { return reviewerId; }
    public void setReviewerId(Integer reviewerId) { this.reviewerId = reviewerId; }

    public Long getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(Long approvedAmount) { this.approvedAmount = approvedAmount; }

    public Integer getApprovedPeriod() { return approvedPeriod; }
    public void setApprovedPeriod(Integer approvedPeriod) { this.approvedPeriod = approvedPeriod; }
}