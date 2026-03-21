package com.ispan.bankmanagement.loan.dao;

import com.ispan.bankmanagement.loan.vo.LoanApplyBean;
import com.ispan.bankmanagement.util.ConnUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanApplyDao {

    // ===============================
    // 🔹 1. 查全部申請
    // ===============================
    public List<LoanApplyBean> getAll() {
        List<LoanApplyBean> list = new ArrayList<>();

        String sql = "SELECT * FROM LOAN_APPLICATION ORDER BY create_time DESC";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoanApplyBean loan = mapRow(rs);
                list.add(loan);
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢貸款資料失敗", e);
        }

        return list;
    }

    // ===============================
    // 🔹 2. 查詢status（條件式）
    // ===============================
    public enum LoanStatus {
        PENDING,
        PENDING_CONFIRM,
        APPROVED,
        REJECTED;
    }

    public List<LoanApplyBean> getByStatus(List<LoanStatus> statusList, String customerId, BigDecimal minAmount, BigDecimal maxAmount) {
        List<LoanApplyBean> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM LOAN_APPLICATION WHERE 1=1 "
        );
        // 🔹 status 條件（支援多個）
        if (statusList != null && !statusList.isEmpty()) {
            sql.append("AND status IN (");
            for (int i = 0; i < statusList.size(); i++) {
                sql.append("?");
                if (i < statusList.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") ");
        }
        if (statusList != null && statusList.isEmpty()) {
            return new ArrayList<>();
        }

        // 附加條件 : 客戶ID
        if (customerId != null && !customerId.isEmpty()) {
            sql.append("AND customer_id = ? ");
        }
        // 附加條件 : 最小貸款金額
        if (minAmount != null) {
            sql.append("AND apply_amount >= ? ");
        }
        // 附加條件 : 最大貸款金額
        if (maxAmount != null) {
            sql.append("AND apply_amount <= ? ");
        }

        sql.append("ORDER BY create_time DESC");

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            // 🔹 status 參數
            if (statusList != null && !statusList.isEmpty()) {
                for (LoanStatus status : statusList) {
                    ps.setString(index++, status.name());
                }
            }

            if (customerId != null && !customerId.isEmpty()) {
                ps.setString(index++, customerId);
            }
            if (minAmount != null) {
                ps.setBigDecimal(index++, minAmount);
            }
            if (maxAmount != null) {
                ps.setBigDecimal(index++, maxAmount);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢失敗", e);
        }

        return list;
    }

    // ===============================
    // 🔹 3. 新增申請
    // ===============================
    public void insert(LoanApplyBean loan) {

        String sql = "INSERT INTO LOAN_APPLICATION " +
                "(application_id, customer_id, apply_amount, apply_period, status, create_time) " +
                "VALUES (?, ?, ?, ?, 'PENDING', GETDATE())";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loan.getApplicationId());
            ps.setString(2, loan.getCustomerId());
            ps.setBigDecimal(3, loan.getApplyAmount());
            ps.setInt(4, loan.getApplyPeriod());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("新增貸款申請失敗", e);
        }
    }

    // ===============================
    // 🔹 4. 核准 & 銀行方案給客戶確認
    // ===============================
    public void submitApproval(LoanApplyBean loan, boolean autoApprove) {

        String sql;

        if (autoApprove){
            // 直接核准
            sql = "UPDATE LOAN_APPLICATION SET " +
                    "approved_amount=?, approved_rate=?, approved_period=?, " +
                    "status='APPROVED', reviewer_id=?, review_time=GETDATE() " +
                    "WHERE application_id=? AND status='PENDING'";
        } else {
            // 需客戶確認
            sql = "UPDATE LOAN_APPLICATION SET " +
                    "approved_amount=?, approved_rate=?, approved_period=?, " +
                    "status='PENDING_CONFIRM', reviewer_id=?, review_time=GETDATE() " +
                    "WHERE application_id=? AND status='PENDING'";
        }

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, loan.getApprovedAmount());
            ps.setBigDecimal(2, loan.getApprovedRate());
            ps.setInt(3, loan.getApprovedPeriod());
            ps.setInt(4, loan.getReviewerId());
            ps.setString(5, loan.getApplicationId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("送出核准失敗", e);
        }
    }

    // ===============================
    // 🔹 5. 銀行拒絕
    // ===============================
    public void rejectByBank(String applicationId, Integer reviewerId) {

        String sql = "UPDATE LOAN_APPLICATION SET " +
                "status='REJECTED', reviewer_id=?, review_time=GETDATE() " +
                "WHERE application_id=? AND status='PENDING'";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reviewerId);
            ps.setString(2, applicationId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("銀行拒絕貸款", e);
        }
    }
    // ===============================
    // 🔹 6. 銀行方案被客戶拒絕
    // ===============================
    public void rejectByCustomer(String applicationId) {

        String sql = "UPDATE LOAN_APPLICATION SET " +
                "status='REJECTED' " +
                "WHERE application_id=? AND status='PENDING_CONFIRM'";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, applicationId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("客戶拒絕失敗", e);
        }
    }

    // ===============================
    // 🔹 7. 共用 mapping
    // ===============================
    private LoanApplyBean mapRow(ResultSet rs) throws SQLException {

        LoanApplyBean loan = new LoanApplyBean();

        loan.setApplicationId(rs.getString("application_id"));
        loan.setCustomerId(rs.getString("customer_id"));
        loan.setApplyAmount(rs.getBigDecimal("apply_amount"));
        loan.setApplyPeriod(rs.getInt("apply_period"));
        loan.setCreateTime(rs.getTimestamp("create_time"));

        loan.setApprovedAmount(rs.getBigDecimal("approved_amount"));
        loan.setApprovedRate(rs.getBigDecimal("approved_rate"));

        int period = rs.getInt("approved_period");
        loan.setApprovedPeriod(rs.wasNull() ? null : period);

        loan.setStatus(rs.getString("status"));

        int reviewer = rs.getInt("reviewer_id");
        loan.setReviewerId(rs.wasNull() ? null : reviewer);

        loan.setReviewTime(rs.getTimestamp("review_time"));

        return loan;
    }
}