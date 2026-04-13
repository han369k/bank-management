//package com.ispan.bankmanagement.account;
//
//import com.ispan.bankmanagement.account.common.exception.ResourceNotFoundException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Account (帳戶) 資料表的資料存取物件 (Data Access Object)。
// * 負責所有與 account 資料表相關的底層 SQL 操作。
// */
//public class AccountDAO {
//
//    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);
//
//    /**
//     * 將 ResultSet 的當前行數據，映射到一個 AccountEntity 物件。
//     * 這是一個私有輔助方法，用於簡化查詢程式碼。
//     * @param rs 資料庫查詢結果集
//     * @return 一個包含該行數據的 AccountEntity 物件
//     * @throws SQLException 如果讀取 ResultSet 時發生錯誤
//     */
//    private AccountEntity mapRow(ResultSet rs) throws SQLException {
//        AccountEntity accountEntity = new AccountEntity();
//        accountEntity.setAccount(rs.getString("account_number"));
//        accountEntity.setCustomerId(rs.getString("customer_id"));
//        accountEntity.setType(rs.getString("type"));
//        accountEntity.setCurrency(rs.getString("currency"));
//        accountEntity.setBalance(rs.getBigDecimal("balance"));
//        accountEntity.setStatus(rs.getString("status"));
//        accountEntity.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
//        if (rs.getTimestamp("change_at") != null) {
//            accountEntity.setChangeAt(rs.getTimestamp("change_at").toLocalDateTime());
//        }
//        return accountEntity;
//    }
//
//    /**
//     * 新增一筆帳戶資料。
//     * 此方法接收一個從 Service 層傳遞過來的 Connection，以確保能參與到同一個資料庫交易中。
//     * @param conn          資料庫連線
//     * @param accountEntity 包含要新增的帳戶資料的實體物件
//     * @throws SQLException 如果 SQL 執行失敗
//     */
//    public void insert(Connection conn, AccountEntity accountEntity) throws SQLException {
//        String sql = "INSERT INTO account (account_number, customer_id, type, currency, balance, status, create_at, change_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//
//        // 使用 try-with-resources 自動關閉 PreparedStatement
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, accountEntity.getAccount());
//            pstmt.setString(2, accountEntity.getCustomerId());
//            pstmt.setString(3, accountEntity.getType());
//            pstmt.setString(4, accountEntity.getCurrency());
//            pstmt.setBigDecimal(5, accountEntity.getBalance());
//            pstmt.setString(6, accountEntity.getStatus());
//            pstmt.setTimestamp(7, Timestamp.valueOf(accountEntity.getCreateAt()));
//
//            if (accountEntity.getChangeAt() != null) {
//                pstmt.setTimestamp(8, Timestamp.valueOf(accountEntity.getChangeAt()));
//            } else {
//                pstmt.setNull(8, Types.TIMESTAMP);
//            }
//
//            pstmt.executeUpdate();
//            logger.debug("成功新增帳戶, account: {}, customerId: {}", accountEntity.getAccount(), accountEntity.getCustomerId());
//        }
//    }
//
//    /**
//     * 透過帳號 (PK) 查詢單筆帳戶資料。
//     * @param conn    資料庫連線
//     * @param account 要查詢的帳號
//     * @return 找到的帳戶實體
//     * @throws SQLException 如果 SQL 執行失敗
//     * @throws ResourceNotFoundException 如果找不到該帳號
//     */
//    public AccountEntity findByAccount(Connection conn, String account) throws SQLException {
//        String sql = "SELECT * FROM account WHERE account_number = ?";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, account);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return mapRow(rs);
//                } else {
//                    // 查無資料時，拋出自訂的例外，讓 Service 層可以捕捉並處理
//                    throw new ResourceNotFoundException("查無此帳戶，account: " + account);
//                }
//            }
//        }
//    }
//
//    /**
//     * 多條件動態查詢。
//     * 根據傳入的 AccountEntity 物件中的非空欄位，動態拼接 SQL 查詢條件。
//     * @param conn          資料庫連線
//     * @param accountEntity 包含查詢條件的實體物件 (可為 null 或空物件)
//     * @return 符合條件的帳戶列表
//     * @throws SQLException 如果 SQL 執行失敗
//     */
//    public List<AccountEntity> query(Connection conn, AccountEntity accountEntity) throws SQLException {
//        // 使用 StringBuilder 動態拼接 SQL，"WHERE 1=1" 技巧方便後續用 "AND" 串接條件
//        StringBuilder sql = new StringBuilder("SELECT * FROM account WHERE 1=1");
//        List<Object> params = new ArrayList<>();
//
//        // 逐一檢查傳入的條件，如果不為 null 或空，就加入 SQL 查詢中
//        if (accountEntity != null && accountEntity.getAccount() != null && !accountEntity.getAccount().trim().isEmpty()) {
//            sql.append(" AND account_number = ?");
//            params.add(accountEntity.getAccount().trim());
//        }
//        if (accountEntity != null && accountEntity.getCustomerId() != null && !accountEntity.getCustomerId().trim().isEmpty()) {
//            sql.append(" AND customer_id = ?");
//            params.add(accountEntity.getCustomerId().trim());
//        }
//        if (accountEntity != null && accountEntity.getType() != null && !accountEntity.getType().trim().isEmpty()) {
//            sql.append(" AND type = ?");
//            params.add(accountEntity.getType().trim());
//        }
//        if (accountEntity != null && accountEntity.getStatus() != null && !accountEntity.getStatus().trim().isEmpty()) {
//            sql.append(" AND status = ?");
//            params.add(accountEntity.getStatus().trim());
//        }
//
//        sql.append(" ORDER BY create_at DESC");
//
//        List<AccountEntity> list = new ArrayList<>();
//        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
//            // 將參數列表中的值，依序設定到 PreparedStatement 中
//            for (int i = 0; i < params.size(); i++) {
//                pstmt.setObject(i + 1, params.get(i));
//            }
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    list.add(mapRow(rs));
//                }
//            }
//        }
//        logger.debug("動態查詢執行完畢, 共查出 {} 筆資料", list.size());
//        return list;
//    }
//
//    /**
//     * 更新帳戶餘額。採用 "balance = balance + ?" 的寫法以避免併發問題 (Race Condition)。
//     * @param conn         資料庫連線
//     * @param account      要更新的帳號
//     * @param amountChange 餘額的變化量 (正數為存款，負數為提款)
//     * @throws SQLException 如果 SQL 執行失敗
//     * @throws ResourceNotFoundException 如果找不到該帳號 (更新了 0 行)
//     */
//    public void updateBalance(Connection conn, String account, java.math.BigDecimal amountChange) throws SQLException {
//        String sql = "UPDATE account SET balance = balance + ?, change_at = ? WHERE account_number = ?";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setBigDecimal(1, amountChange);
//            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
//            pstmt.setString(3, account);
//
//            int affectedRows = pstmt.executeUpdate();
//
//            // 如果沒有任何行被更新，代表該帳號不存在
//            if (affectedRows == 0) {
//                throw new ResourceNotFoundException("更新餘額失敗，帳戶不存在，account: " + account);
//            }
//            logger.debug("餘額更新成功, account: {}, 變化量: {}", account, amountChange);
//        }
//    }
//
//    /**
//     * 更新帳戶狀態 (例如：ACTIVE, FROZEN)。
//     * @param conn    資料庫連線
//     * @param account 要更新的帳號
//     * @param status  新的狀態
//     * @throws SQLException 如果 SQL 執行失敗
//     * @throws ResourceNotFoundException 如果找不到該帳號 (更新了 0 行)
//     */
//    public void updateStatus(Connection conn, String account, String status) throws SQLException {
//        String sql = "UPDATE account SET status = ?, change_at = ? WHERE account_number = ?";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, status);
//            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
//            pstmt.setString(3, account);
//
//            int affectedRows = pstmt.executeUpdate();
//
//            if (affectedRows == 0) {
//                throw new ResourceNotFoundException("更新狀態失敗，帳戶不存在，account: " + account);
//            }
//            logger.debug("狀態更新成功, account: {}, 新狀態: {}", account, status);
//        }
//    }
//
//    /**
//     * 實體刪除一筆帳戶資料。
//     * (專題展示用，實務上極少使用)
//     * @param conn    資料庫連線
//     * @param account 要刪除的帳號
//     * @throws SQLException 如果 SQL 執行失敗
//     * @throws ResourceNotFoundException 如果找不到該帳號 (刪除了 0 行)
//     */
//    public void deleteByAccount(Connection conn, String account) throws SQLException {
//        String sql = "DELETE FROM account WHERE account_number = ?";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, account);
//
//            int affectedRows = pstmt.executeUpdate();
//
//            if (affectedRows == 0) {
//                throw new ResourceNotFoundException("刪除失敗，帳戶不存在，account: " + account);
//            }
//            // 因為是危險操作，使用 warn 層級來記錄
//            logger.warn("警告: 已實體刪除帳戶, account: {}", account);
//        }
//    }
//
//}
