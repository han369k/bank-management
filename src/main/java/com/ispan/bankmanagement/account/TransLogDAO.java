//package com.ispan.bankmanagement.account;
//
//import com.ispan.bankmanagement.account.common.exception.ResourceNotFoundException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.*;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * TransLog (交易紀錄) 資料表的資料存取物件 (Data Access Object)。
// * 負責所有與 trans_log 資料表相關的底層 SQL 操作。
// */
//public class TransLogDAO {
//
//    private static final Logger logger = LoggerFactory.getLogger(TransLogDAO.class);
//
//    /**
//     * 將 ResultSet 的當前行數據，映射到一個 TransLog 物件。
//     */
//    private TransLog mapRow(ResultSet rs) throws SQLException {
//        TransLog vo = new TransLog();
//        vo.setTransLogId(rs.getLong("trans_log_id"));
//        vo.setReferenceId(rs.getString("reference_id"));
//        vo.setAmount(rs.getBigDecimal("amount"));
//        vo.setType(rs.getString("type"));
//        vo.setOperationAccount(rs.getString("operation_account"));
//        vo.setOtherAccount(rs.getString("other_account"));
//        vo.setBalance(rs.getBigDecimal("balance"));
//        vo.setTransactionTime(rs.getTimestamp("transaction_time").toLocalDateTime());
//        vo.setNote(rs.getString("note"));
//        return vo;
//    }
//
//    /**
//     * 新增一筆交易紀錄。
//     * @param conn 資料庫連線，由 Service 層傳入以確保交易一致性
//     * @param log  要新增的交易紀錄實體
//     * @throws SQLException 如果 SQL 執行失敗
//     */
//    public void insert(Connection conn, TransLog log) throws SQLException {
//        String sql = "INSERT INTO trans_log (reference_id, amount, type, operation_account, other_account, balance, transaction_time, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, log.getReferenceId());
//            pstmt.setBigDecimal(2, log.getAmount());
//            pstmt.setString(3, log.getType());
//            pstmt.setString(4, log.getOperationAccount());
//            pstmt.setString(5, log.getOtherAccount());
//            pstmt.setBigDecimal(6, log.getBalance());
//            pstmt.setTimestamp(7, Timestamp.valueOf(log.getTransactionTime()));
//            pstmt.setString(8, log.getNote());
//            pstmt.executeUpdate();
//            logger.debug("成功寫入交易紀錄, referenceId: {}", log.getReferenceId());
//        }
//    }
//
//    /**
//     * 透過 Reference ID 查詢單筆交易紀錄。
//     * @param conn        資料庫連線
//     * @param referenceId 交易參考編號
//     * @return 找到的交易紀錄實體
//     * @throws SQLException 如果 SQL 執行失敗
//     * @throws ResourceNotFoundException 如果找不到該紀錄
//     */
//    public TransLog findByReferenceId(Connection conn, String referenceId) throws SQLException {
//        String sql = "SELECT * FROM trans_log WHERE reference_id = ?";
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, referenceId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return mapRow(rs);
//                }
//                throw new ResourceNotFoundException("查無此交易紀錄，referenceId: " + referenceId);
//            }
//        }
//    }
//
//    /**
//     * 動態條件查詢交易紀錄，並支援分頁。
//     * @param conn       資料庫連線
//     * @param customerId (可選) 客戶 ID
//     * @param account    (可選) 帳號
//     * @param startDate  (可選) 開始日期
//     * @param endDate    (可選) 結束日期
//     * @param page       頁碼 (從 1 開始)
//     * @return 符合條件的交易紀錄列表
//     * @throws SQLException 如果 SQL 執行失敗
//     */
//    public List<TransLog> query(Connection conn, String customerId, String account, LocalDate startDate, LocalDate endDate, int page) throws SQLException {
//        final int PAGE_SIZE = 50;
//        StringBuilder sql = new StringBuilder("SELECT t.* FROM trans_log t ");
//        List<Object> params = new ArrayList<>();
//
//        // 如果提供了 customerId，則需要與 account 表進行 JOIN
//        if (customerId != null && !customerId.trim().isEmpty()) {
//            sql.append("JOIN account a ON t.operation_account = a.account_number WHERE a.customer_id = ?");
//            params.add(customerId.trim());
//        } else {
//            sql.append("WHERE 1=1");
//        }
//
//        if (account != null && !account.trim().isEmpty()) {
//            sql.append(" AND t.operation_account = ?");
//            params.add(account.trim());
//        }
//        if (startDate != null) {
//            sql.append(" AND t.transaction_time >= ?");
//            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
//        }
//        if (endDate != null) {
//            // 查詢條件通常包含當天，所以結束日期要加一天
//            sql.append(" AND t.transaction_time < ?");
//            params.add(Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
//        }
//
//        // 分頁查詢必須搭配排序
//        sql.append(" ORDER BY t.transaction_time DESC");
//        // 使用 SQL Server 2012+ 的 OFFSET-FETCH 語法進行分頁
//        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
//        params.add((page - 1) * PAGE_SIZE);
//        params.add(PAGE_SIZE);
//
//        List<TransLog> list = new ArrayList<>();
//        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
//            for (int i = 0; i < params.size(); i++) {
//                pstmt.setObject(i + 1, params.get(i));
//            }
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    list.add(mapRow(rs));
//                }
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 計算符合動態條件的總筆數，用於分頁。
//     * @param conn       資料庫連線
//     * @param customerId (可選) 客戶 ID
//     * @param account    (可選) 帳號
//     * @param startDate  (可選) 開始日期
//     * @param endDate    (可選) 結束日期
//     * @return 符合條件的總筆數
//     * @throws SQLException 如果 SQL 執行失敗
//     */
//    public int count(Connection conn, String customerId, String account, LocalDate startDate, LocalDate endDate) throws SQLException {
//        // 這裡的 WHERE 條件邏輯必須與 query() 方法完全一致，才能保證總筆數的正確性
//        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM trans_log t ");
//        List<Object> params = new ArrayList<>();
//
//        if (customerId != null && !customerId.trim().isEmpty()) {
//            sql.append("JOIN account a ON t.operation_account = a.account_number WHERE a.customer_id = ?");
//            params.add(customerId.trim());
//        } else {
//            sql.append("WHERE 1=1");
//        }
//
//        if (account != null && !account.trim().isEmpty()) {
//            sql.append(" AND t.operation_account = ?");
//            params.add(account.trim());
//        }
//        if (startDate != null) {
//            sql.append(" AND t.transaction_time >= ?");
//            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
//        }
//        if (endDate != null) {
//            sql.append(" AND t.transaction_time < ?");
//            params.add(Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
//        }
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
//            for (int i = 0; i < params.size(); i++) {
//                pstmt.setObject(i + 1, params.get(i));
//            }
//            try (ResultSet rs = pstmt.executeQuery()) {
//                return rs.next() ? rs.getInt(1) : 0;
//            }
//        }
//    }
//
//    /**
//     * 透過 Reference ID 實體刪除交易紀錄 (專題用)。
//     * @param conn        資料庫連線
//     * @param referenceId 要刪除的交易參考編號
//     * @throws SQLException 如果 SQL 執行失敗
//     * @throws ResourceNotFoundException 如果找不到該紀錄 (刪除了 0 行)
//     */
//    public void deleteByReferenceId(Connection conn, String referenceId) throws SQLException {
//        String sql = "DELETE FROM trans_log WHERE reference_id = ?";
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, referenceId);
//            int affectedRows = pstmt.executeUpdate();
//            if (affectedRows == 0) {
//                throw new ResourceNotFoundException("刪除失敗，交易紀錄不存在，referenceId: " + referenceId);
//            }
//            logger.warn("警告: 透過 Reference ID 實體刪除了一筆交易紀錄, referenceId: {}", referenceId);
//        }
//    }
//
//    // 以下是舊的、功能較單一的查詢方法，予以保留，以防有其他地方呼叫
//    public List<TransLog> findByOperationAccount(Connection conn, String operationAccount) throws SQLException {
//        String sql = "SELECT * FROM trans_log WHERE operation_account = ? ORDER BY transaction_time DESC";
//        List<TransLog> list = new ArrayList<>();
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, operationAccount);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    list.add(mapRow(rs));
//                }
//            }
//        }
//        return list;
//    }
//    public List<TransLog> findByCustomerId(Connection conn, String customerId) throws SQLException {
//        String sql = "SELECT t.* FROM trans_log t JOIN account a ON t.operation_account = a.account WHERE a.customer_id = ? ORDER BY t.transaction_time DESC";
//        List<TransLog> list = new ArrayList<>();
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, customerId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    list.add(mapRow(rs));
//                }
//            }
//        }
//        return list;
//    }
//}
