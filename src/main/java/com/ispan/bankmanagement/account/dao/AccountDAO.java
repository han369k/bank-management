package com.ispan.bankmanagement.account.dao;

import com.ispan.bankmanagement.account.vo.Account;

// 導入 SLF4J 套件 準備記錄底層執行的軌跡
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // 建立 logger 物件 方便除錯時不用瞎子摸象
    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);

    // 查詢用 將DB回傳的rs物件接到Java物件
    // 寫成共用方法
    private Account mapRow(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccount(rs.getString("account"));
        account.setCustomerId(rs.getString("customer_id"));
        account.setType(rs.getString("type"));
        account.setCurrency(rs.getString("currency"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(rs.getString("status"));
        account.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
        if (rs.getTimestamp("change_at") != null) {
            account.setChangeAt(rs.getTimestamp("change_at").toLocalDateTime());
        }
        return account;
    }

    // 新增帳戶 用 "service" 傳來的conn
    /**
     * 為什麼不在這裡用getConnection? 這是要考量到單支service 可能同時會呼叫到多種dao方法
     * 如果每個方法執行時都使用獨立的Connection 這樣就沒有辦法充分利用資料庫的“保護寫入”特性
     * 保護寫入 就是如果該次寫入完成前 會將該筆資料鎖住 保護資料不同時被其他程式改寫
     * *** 任何update 完成前都會鎖住該筆 row ***
     * 以此來滿足於資料的原子性與一致性
     */

    public void insert(Connection conn, Account account) throws SQLException {
        String sql = "INSERT INTO account (account, customer_id, type, currency, balance, status, create_at, change_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // 只有 pstmt 需要放在 try-with-resources 自動關閉，conn 絕對不能在這裡關 誰開的誰關
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccount());
            pstmt.setString(2, account.getCustomerId());
            pstmt.setString(3, account.getType());
            pstmt.setString(4, account.getCurrency());
            pstmt.setBigDecimal(5, account.getBalance());
            pstmt.setString(6, account.getStatus());
            pstmt.setTimestamp(7, Timestamp.valueOf(account.getCreateAt()));

            if (account.getChangeAt() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(account.getChangeAt()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP);
            }

            // 執行寫入 DB
            pstmt.executeUpdate();

            // 寫入成功後留個 log 紀錄 證明資料有吃進去
            logger.debug("成功新增帳戶, account: {}, customerId: {}", account.getAccount(), account.getCustomerId());
        }
    }

    // 查詢單筆帳戶
    public Account findByAccount(Connection conn, String account) throws SQLException {
        String sql = "SELECT * FROM account WHERE account = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        // 查無資料就印個 warning 提醒一下
        logger.warn("查無此帳戶, account: {}", account);
        return null;
    }

    // 多條件動態查詢
    public List<Account> query(Connection conn, Account account) throws SQLException {
        // 透過StringBuilder 實做動態拼接SQL條件 用 1=1 來做連接 (永遠為true)
        StringBuilder sql = new StringBuilder("SELECT * FROM account WHERE 1=1");

        List<Object> params = new ArrayList<>();

        // 擋 null, 完全空的字串, 只有空白的字串
        if (account.getAccount() != null && !account.getAccount().trim().isEmpty()) {
            sql.append(" AND account = ?");
            params.add(account.getAccount().trim());
        }
        if (account.getCustomerId() != null && !account.getCustomerId().trim().isEmpty()) {
            sql.append(" AND customer_id = ?");
            params.add(account.getCustomerId().trim());
        }
        if (account.getType() != null && !account.getType().trim().isEmpty()) {
            sql.append(" AND type = ?");
            params.add(account.getType().trim());
        }
        if (account.getStatus() != null && !account.getStatus().trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(account.getStatus().trim());
        }

        sql.append(" ORDER BY create_at DESC");

        List<Account> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        // 紀錄一下這次動態查詢總共撈了幾筆出來
        logger.debug("動態查詢執行完畢, 共查出 {} 筆資料", list.size());
        return list;
    }

    // 更新餘額 (解決 Race Condition 的寫法，交由 DB 算變化量)
    public boolean updateBalance(Connection conn, String account, java.math.BigDecimal amountChange) throws SQLException {
        String sql = "UPDATE account SET balance = balance + ?, change_at = ? WHERE account = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, amountChange);
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setString(3, account);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.debug("餘額更新成功, account: {}, 變化量: {}", account, amountChange);
                return true;
            } else {
                logger.warn("餘額更新失敗 可能是帳戶不存在, account: {}", account);
                return false;
            }
        }
    }

    // 更新狀態 ex: 正常,帳戶凍結,註銷
    public boolean updateStatus(Connection conn, String account, String status) throws SQLException {
        String sql = "UPDATE account SET status = ?, change_at = ? WHERE account = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setString(3, account);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.debug("狀態更新成功, account: {}, 新狀態: {}", account, status);
                return true;
            }
            return false;
        }
    }

    // 實體刪除帳戶
    // *** 因應符合專題評分要求 只為單次展示 除外禁止呼叫此方法
    public boolean deleteByAccount(Connection conn, String account) throws SQLException {
        String sql = "DELETE FROM account WHERE account = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account);

            int affectedRows = pstmt.executeUpdate();

            // 因為是危險操作 改用 warn 層級來記錄
            if (affectedRows > 0) {
                logger.warn("警告: 已實體刪除帳戶, account: {}", account);
                return true;
            }
            return false;
        }
    }

}