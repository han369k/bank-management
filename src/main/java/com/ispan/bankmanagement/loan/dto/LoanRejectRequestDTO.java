package com.ispan.bankmanagement.loan.dto;

import jakarta.validation.constraints.NotNull;

public class LoanRejectRequestDTO {

    @NotNull(message = "審核行員代號不可為空")
    private Integer reviewerId;

    // 💡 Table 無 rejectReason 欄位，若未來有需求可在此擴充

    // Getters & Setters
    public Integer getReviewerId() { return reviewerId; }
    public void setReviewerId(Integer reviewerId) { this.reviewerId = reviewerId; }
}