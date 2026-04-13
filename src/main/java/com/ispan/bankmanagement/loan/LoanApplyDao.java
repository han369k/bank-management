package com.ispan.bankmanagement.loan;

import com.ispan.bankmanagement.common.util.ConnUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanApplyDao {

    // ===============================
    // 🔹 統一處理狀態
    // ===============================
    public enum LoanStatus {
        PENDING,
        PENDING_CONFIRM,
        APPROVED,
        REJECTED;
    }

    private void updateStatus(Connection conn,
                              String applicationId,
                              LoanStatus from,
                              LoanStatus to) {

        String sql = """
                    UPDATE LOAN_APPLICATION
                    SET status = ?, review_time = SYSDATETIME()
                    WHERE application_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, to.name()); // to.name() = enum → 字串
            ps.setString(2, applicationId);

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
    // 🔹 2. 依據status查詢
    // ===============================
    public List<LoanApplyBean> getByStatus(List<LoanStatus> statusList,
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

        String sql = "INSERT INTO LOAN_APPLICATION\n" +
                "(application_id, customer_id, apply_type, apply_amount, apply_period, rate, status, create_time)\n" +
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

            if (loan.getApprovedAmount() != null) {
                ps.setLong(1, loan.getApprovedAmount());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setBigDecimal(2, loan.getApprovedRate());

            if (loan.getApprovedPeriod() != null) {
                ps.setInt(3, loan.getApprovedPeriod());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

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

            if (loan.getApprovedAmount() != null) {
                ps.setLong(1, loan.getApprovedAmount());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setBigDecimal(2, loan.getApprovedRate());

            if (loan.getApprovedPeriod() != null) {
                ps.setInt(3, loan.getApprovedPeriod());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

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
    // 🔹 10. 刪除指定狀態的所有資料
    // ===============================
    public void deleteByStatus(LoanStatus status) {

        String sql = "DELETE FROM LOAN_APPLICATION WHERE status = ?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            int rows = ps.executeUpdate();

            System.out.println("已刪除 " + rows + " 筆狀態為 " + status.name() + " 的資料");

        } catch (SQLException e) {
            throw new RuntimeException("刪除資料失敗", e);
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

        loan.setStatus(rs.getString("status"));

        int reviewer = rs.getInt("reviewer_id");
        loan.setReviewerId(rs.wasNull() ? null : reviewer);

        loan.setReviewTime(rs.getTimestamp("review_time"));

        return loan;
    }
}