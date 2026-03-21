package com.ispan.bankmanagement.account.dao;

import com.ispan.bankmanagement.account.entity.TransLogEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransLogDAO {

    private static final Logger logger = LoggerFactory.getLogger(TransLogDAO.class);

    // 把 ResultSet 轉換成共用的方法
    private TransLogEntity mapRow(ResultSet rs) throws SQLException {
        TransLogEntity vo = new TransLogEntity();
        vo.setTransLogId(rs.getLong("trans_log_id"));
        vo.setReferenceId(rs.getString("reference_id"));
        vo.setAmount(rs.getBigDecimal("amount"));
        vo.setType(rs.getString("type"));
        vo.setOperationAccount(rs.getString("operation_account"));
        vo.setOtherAccount(rs.getString("other_account"));
        vo.setBalance(rs.getBigDecimal("balance"));
        vo.setTransactionTime(rs.getTimestamp("transaction_time").toLocalDateTime());
        vo.setNote(rs.getString("note"));
        return vo;
    }

    // 新增交易紀錄
    // 注意: trans_log_id 是 identity，會自動遞增，所以不寫入 INSERT 語句中
    public void insert(Connection conn, TransLogEntity log) throws SQLException {
        String sql = "INSERT INTO trans_log (reference_id, amount, type, operation_account, other_account, balance, transaction_time, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, log.getReferenceId());
            pstmt.setBigDecimal(2, log.getAmount());
            pstmt.setString(3, log.getType());
            pstmt.setString(4, log.getOperationAccount());
            pstmt.setString(5, log.getOtherAccount());
            pstmt.setBigDecimal(6, log.getBalance());
            pstmt.setTimestamp(7, Timestamp.valueOf(log.getTransactionTime()));
            pstmt.setString(8, log.getNote());

            pstmt.executeUpdate();
            logger.debug("成功寫入交易紀錄, referenceId: {}", log.getReferenceId());
        }
    }

    // 透過流水號 (PK) 查詢單筆交易紀錄
    public TransLogEntity findById(Connection conn, Long transLogId) throws SQLException {
        String sql = "SELECT * FROM trans_log WHERE trans_log_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, transLogId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // 透過 Reference ID 查詢單筆交易紀錄 (Reference ID 應該是唯一的)
    public TransLogEntity findByReferenceId(Connection conn, String referenceId) throws SQLException {
        String sql = "SELECT * FROM trans_log WHERE reference_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // 查詢特定帳戶的所有交易紀錄 (依時間由新到舊排序)
    public List<TransLogEntity> findByOperationAccount(Connection conn, String operationAccount) throws SQLException {
        String sql = "SELECT * FROM trans_log WHERE operation_account = ? ORDER BY transaction_time DESC";
        List<TransLogEntity> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, operationAccount);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // 透過 Customer ID 查詢其名下所有帳戶的交易紀錄
    public List<TransLogEntity> findByCustomerId(Connection conn, String customerId) throws SQLException {
        // 使用 JOIN 查詢，連接 trans_log 和 account 兩個表
        String sql = "SELECT t.* FROM trans_log t " +
                     "JOIN account a ON t.operation_account = a.account " +
                     "WHERE a.customer_id = ? " +
                     "ORDER BY t.transaction_time DESC";
        List<TransLogEntity> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }


    // 修改交易紀錄的備註
    // 專題用。實務上金融業嚴格禁止修改交易金額或時間，最多只能改備註
    public boolean updateNote(Connection conn, Long transLogId, String newNote) throws SQLException {
        String sql = "UPDATE trans_log SET note = ? WHERE trans_log_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNote);
            pstmt.setLong(2, transLogId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.debug("成功更新交易紀錄備註, transLogId: {}", transLogId);
                return true;
            }
            return false;
        }
    }

    // 實體刪除交易紀錄
    // (專題用。實務上金融業絕對不允許 DELETE 交易紀錄)
    public boolean deleteById(Connection conn, Long transLogId) throws SQLException {
        String sql = "DELETE FROM trans_log WHERE trans_log_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, transLogId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.warn("警告: 實體刪除了一筆交易紀錄, transLogId: {}", transLogId);
                return true;
            }
            return false;
        }
    }

    // 透過 Reference ID 實體刪除交易紀錄 (專題用)
    public boolean deleteByReferenceId(Connection conn, String referenceId) throws SQLException {
        String sql = "DELETE FROM trans_log WHERE reference_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.warn("警告: 透過 Reference ID 實體刪除了一筆交易紀錄, referenceId: {}", referenceId);
                return true;
            }
            return false;
        }
    }
}