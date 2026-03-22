package com.ispan.bankmanagement.loan.service;

import com.ispan.bankmanagement.loan.dao.LoanApplyDao;
import com.ispan.bankmanagement.loan.dao.LoanApplyDao.LoanStatus;
import com.ispan.bankmanagement.loan.vo.LoanApplyBean;

import java.math.BigDecimal;
import java.util.List;

public class LoanApplyService {

    private LoanApplyDao loanDao = new LoanApplyDao();

    // ===============================
    // 🔹 1. 查詢（後台 / 前台共用）
    // ===============================
    public List<LoanApplyBean> getLoans(List<LoanStatus> statusList,
                                        String customerId,
                                        BigDecimal minAmount,
                                        BigDecimal maxAmount) {

        return loanDao.getByStatus(statusList, customerId, minAmount, maxAmount);
    }

    // ===============================
    // 🔹 2. 客戶申請貸款
    // ===============================
    public void applyLoan(LoanApplyBean loan) {

        // ⭐ 基本驗證
        if (loan.getApplyAmount() == null ||
                loan.getApplyAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("申請金額必須大於0");
        }

        if (loan.getApplyPeriod() == null || loan.getApplyPeriod() <= 0) {
            throw new RuntimeException("期數必須大於0");
        }

        loanDao.insert(loan);
    }

    // ===============================
    // 🔹 3. 銀行審核（核心🔥）
    // ===============================
    public void processApproval(LoanApplyBean loan) {

        // ⭐ 判斷是否自動核准
        boolean autoApprove = checkAutoApprove(loan);

        if (autoApprove) {
            loanDao.approveDirectly(loan);
        } else {
            loanDao.submitForConfirmation(loan);
        }
    }

    // ===============================
    // 🔹 4. 自動核准規則（可擴充🔥）
    // ===============================
    private boolean checkAutoApprove(LoanApplyBean loan) {

        // 小額貸款直接核准
        if (loan.getApplyAmount().compareTo(new BigDecimal("50000")) <= 0) {
            return true;
        }

        // 利率夠低（優惠方案）直接核准
        if (loan.getApprovedRate() != null &&
                loan.getApprovedRate().compareTo(new BigDecimal("2.0")) <= 0) {
            return true;
        }

        return false;
    }

    // ===============================
    // 🔹 5. 客戶確認（最終核准）
    // ===============================
    public void confirmApproval(String applicationId) {

        if (applicationId == null || applicationId.isEmpty()) {
            throw new RuntimeException("申請編號不可為空");
        }

        loanDao.confirmApproval(applicationId);
    }

    // ===============================
    // 🔹 6. 銀行拒絕
    // ===============================
    public void rejectByBank(String applicationId, Integer reviewerId) {

        if (applicationId == null || applicationId.isEmpty()) {
            throw new RuntimeException("申請編號不可為空");
        }

        if (reviewerId == null) {
            throw new RuntimeException("審核人不可為空");
        }

        loanDao.rejectByBank(applicationId, reviewerId);
    }

    // ===============================
    // 🔹 7. 客戶拒絕
    // ===============================
    public void rejectByCustomer(String applicationId) {

        if (applicationId == null || applicationId.isEmpty()) {
            throw new RuntimeException("申請編號不可為空");
        }

        loanDao.rejectByCustomer(applicationId);
    }
}