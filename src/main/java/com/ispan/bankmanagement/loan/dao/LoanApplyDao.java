package com.ispan.bankmanagement.loan.dao;

import com.ispan.bankmanagement.loan.vo.LoanApplyBean;
import com.ispan.bankmanagement.util.ConnUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanApplyDao {

    public enum LoanStatus {
        PENDING,
        PENDING_CONFIRM,
        APPROVED,
        REJECTED;
    }

    // ===============================
    // 統一狀態修改方法
    // ===============================
    private void updateStatus(Connection conn,
                              String applicationId,
                              LoanStatus from,
                              LoanStatus to) {

        String sql = """
                    UPDATE LOAN_APPLICATION
                    SET status = ?, review_time = SYSDATETIME()
                    WHERE application_id = ? AND status = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, to.name());
            ps.setString(2, applicationId);
            ps.setString(3, from.name());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("狀態錯誤，轉換失敗");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


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
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢貸款申請資料失敗", e);
        }

        return list;
    }

    // ===============================
    // 🔹 2. 查詢status（條件式）
    // ===============================
    public List<LoanApplyBean> getByStatus(List<LoanStatus> statusList,
                                           String customerId,
                                           Long minAmount,
                                           Long maxAmount) {

        List<LoanApplyBean> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM LOAN_APPLICATION WHERE 1=1 "
        );

        if (statusList != null) {
            sql.append("AND status IN (");
            for (int i = 0; i < statusList.size(); i++) {
                sql.append("?");
                if (i < statusList.size() - 1) sql.append(",");
            }
            sql.append(") ");
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
            if (statusList != null) {
                for (LoanStatus status : statusList) {
                    ps.setString(index++, status.name());
                }
            }

            if (customerId != null && !customerId.isEmpty()) {
                ps.setString(index++, customerId);
            }
            if (minAmount != null) {
                ps.setLong(index++, minAmount);
            }
            if (maxAmount != null) {
                ps.setLong(index++, maxAmount);
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
                "(application_id, customer_id, apply_type, apply_amount, apply_period, rate, status, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATETIME())";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loan.getApplicationId());
            ps.setString(2, loan.getCustomerId());
            ps.setString(3, loan.getApplyType());
            if (loan.getApplyAmount() != null) {
                ps.setLong(4, loan.getApplyAmount());
            } else {
                ps.setNull(4, Types.BIGINT);
            }
            ps.setInt(5, loan.getApplyPeriod());
            ps.setBigDecimal(6, loan.getRate());
            ps.setString(7, LoanStatus.PENDING.name());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("新增貸款申請失敗", e);
        }
    }

    // ===============================
    // 🔹 4. 銀行直接核准
    // ===============================
    public void approveDirectly(LoanApplyBean loan) {

        String sql = "UPDATE LOAN_APPLICATION SET " +
                "approved_amount=?, approved_rate=?, approved_period=?, " +
                "reviewer_id=?, review_time=SYSDATETIME() " +
                "WHERE application_id=?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, loan.getApprovedAmount());
            ps.setBigDecimal(2, loan.getApprovedRate());
            ps.setInt(3, loan.getApprovedPeriod());
            ps.setInt(4, loan.getReviewerId());
            ps.setString(5, loan.getApplicationId());

            ps.executeUpdate();
            updateStatus(conn,
                    loan.getApplicationId(),
                    LoanStatus.PENDING,
                    LoanStatus.APPROVED);

        } catch (SQLException e) {
            throw new RuntimeException("直接核准失敗", e);
        }
    }

    // ===============================
    // 🔹 5. 銀行方案給客戶確認
    // ===============================
    public void submitForConfirm(LoanApplyBean loan) {

        String sql = "UPDATE LOAN_APPLICATION SET " +
                "approved_amount=?, approved_rate=?, approved_period=?, " +
                "reviewer_id=?, review_time=SYSDATETIME() " +
                "WHERE application_id=?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, loan.getApprovedAmount());
            ps.setBigDecimal(2, loan.getApprovedRate());
            ps.setInt(3, loan.getApprovedPeriod());
            ps.setInt(4, loan.getReviewerId());
            ps.setString(5, loan.getApplicationId());

            ps.executeUpdate();

            updateStatus(conn,
                    loan.getApplicationId(),
                    LoanStatus.PENDING,
                    LoanStatus.PENDING_CONFIRM);

            } catch (SQLException e) {
            throw new RuntimeException("送出方案失敗", e);
            }
    }

    // ===============================
    // 🔹 6. 客戶確認（最終核准）
    // ===============================
    public void confirmApproval(String applicationId) {

        try (Connection conn = ConnUtil.getConn()) {

            // ⭐ 修改
            updateStatus(conn,
                    applicationId,
                    LoanStatus.PENDING_CONFIRM,
                    LoanStatus.APPROVED);

        } catch (SQLException e) {
            throw new RuntimeException("客戶確認失敗", e);
        }
    }

    // ===============================
    // 🔹 7. 銀行拒絕
    // ===============================
    public void rejectByBank(String applicationId, Integer reviewerId) {

        String sql = "UPDATE LOAN_APPLICATION SET " +
                "reviewer_id=?, review_time=SYSDATETIME() " +
                "WHERE application_id=?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reviewerId);
            ps.setString(2, applicationId);

            ps.executeUpdate();

            updateStatus(conn,
                    applicationId,
                    LoanStatus.PENDING,
                    LoanStatus.REJECTED);

        } catch (SQLException e) {
            throw new RuntimeException("銀行拒絕貸款", e);
        }
    }

    // ===============================
    // 🔹 8. 銀行方案被客戶拒絕
    // ===============================
    public void rejectByCustomer(String applicationId) {

        try (Connection conn = ConnUtil.getConn()) {

            updateStatus(conn,
                    applicationId,
                    LoanStatus.PENDING_CONFIRM,
                    LoanStatus.REJECTED);

        } catch (SQLException e) {
            throw new RuntimeException("客戶拒絕失敗", e);
        }
    }

    // ===============================
    // 🔹 9. 共用 mapping
    // ===============================
    private LoanApplyBean mapRow(ResultSet rs) throws SQLException {

        LoanApplyBean loan = new LoanApplyBean();

        loan.setApplicationId(rs.getString("application_id"));
        loan.setCustomerId(rs.getString("customer_id"));

        loan.setApplyType(rs.getString("apply_type"));

        long applyAmount = rs.getLong("apply_amount");
        loan.setApplyAmount(rs.wasNull() ? null : applyAmount);

        loan.setApplyPeriod(rs.getInt("apply_period"));
        loan.setRate(rs.getBigDecimal("rate"));
        loan.setCreateTime(rs.getTimestamp("create_time"));

        long approvedAmount = rs.getLong("approved_amount");
        loan.setApprovedAmount(rs.wasNull() ? null : approvedAmount);

        loan.setApprovedRate(rs.getBigDecimal("approved_rate"));

        int period = rs.getInt("approved_period");
        loan.setApprovedPeriod(rs.wasNull() ? null : period);

        // ⭐ 修改：String → enum（前提：Bean 要改）
        loan.setStatus(rs.getString("status")); // ← 如果你還沒改 Bean 就先保留

        int reviewer = rs.getInt("reviewer_id");
        loan.setReviewerId(rs.wasNull() ? null : reviewer);

        loan.setReviewTime(rs.getTimestamp("review_time"));

        return loan;
    }

}

