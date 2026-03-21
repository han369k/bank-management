package com.ispan.bankmanagement.account.service;


import com.ispan.bankmanagement.account.dao.AccountDAO;
import com.ispan.bankmanagement.account.dao.TransLogDAO;
import com.ispan.bankmanagement.account.entity.AccountEntity;
import com.ispan.bankmanagement.account.entity.TransLogEntity;
import com.ispan.bankmanagement.util.ConnUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    AccountDAO accountDAO = new AccountDAO();
    TransLogDAO transLogDAO = new TransLogDAO();

    // 新增帳戶
    public void createAcc(AccountEntity accountEntity) {
        try (Connection conn = ConnUtil.getConn()) {

            // 檢查帳戶資料
            if (accountEntity == null || accountEntity.getAccount() == null || accountEntity.getAccount().trim().isEmpty()) {
                logger.warn("新增失敗，提供的資料格式不正確。");
                throw new IllegalArgumentException("帳戶資料與帳號不可為空。");
            }
            // 檢查帳號是否已存在
            if (accountDAO.findByAccount(conn, accountEntity.getAccount()) != null) {
                // 這是一個業務邏輯錯誤，不是資料庫錯誤
                logger.warn("嘗試新增已存在的帳戶: {}", accountEntity.getAccount());
                throw new IllegalArgumentException("帳號 " + accountEntity.getAccount() + " 已存在，無法重複新增。");
            }

            // 限制帳號長度為12碼
            if (accountEntity.getAccount().trim().length() != 12) {
                throw new IllegalArgumentException("帳號格式不合法，長度應為12碼");
            }

            accountDAO.insert(conn, accountEntity);
            logger.info("帳戶 {} 建立成功。", accountEntity.getAccount());
        } catch (SQLException e) {
            throw new RuntimeException("連線失敗，請稍後再試。", e);
        }
    }

    // 用帳號查
    public AccountEntity searchAccountByAccount(String account) {
        try (Connection conn = ConnUtil.getConn()) {
            AccountEntity acc = accountDAO.findByAccount(conn, account);

            // DAO回傳null 代表檢查不到
            if (acc == null) {
                logger.warn("查詢的帳戶 {} 不存在。", account);
                throw new RuntimeException("查無此帳戶: " + account);
            }

            logger.info("成功查詢到帳戶: {}", account);
            return acc;

        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("連線失敗，請稍後再試。", e);
        }
    }

    // todo: 需要改寫DAO (join custormer)
    //public AccountEntity searchAccountByCustormerName(){}

    // 查全部
    public List<AccountEntity> searchAllAccount(AccountEntity accountEntity) {
        try (Connection conn = ConnUtil.getConn()) {
            List<AccountEntity> list = accountDAO.query(conn, accountEntity);

            logger.info("帳戶條件查詢完成，共找到 {} 筆資料。", list.size());
            return list;
        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("連線失敗，請稍後再試。", e);
        }
    }

    // 狀態更新
    // todo: insert adminLog
    public void updateStatus(String accountNo, String status) {
        try (Connection conn = ConnUtil.getConn()) {
            // 防呆 查帳號是否存在
            AccountEntity accountEntity = accountDAO.findByAccount(conn, accountNo);

            if (accountEntity == null || accountEntity.getAccount() == null || accountEntity.getAccount().trim().isEmpty()) {
                logger.warn("編輯失敗， {} 不存在。", accountNo);
                throw new RuntimeException("編輯失敗，帳號不存在。");
            }

            if (accountDAO.updateStatus(conn, accountNo, status)) {
                logger.info("狀態更新成功, account: {}, 新狀態: {}", accountNo, status);
            } else {
                logger.warn("帳號更新失敗");
                throw new RuntimeException("狀態更新失敗，請確認logger，並重新嘗試");
            }


        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException(e);
        }
    }


    // 為了對應專題的CRUD展示而實作
    // 沒事切記不要使用這個method
    // Demo時記得要刪沒有任何交易紀錄的帳號
    public void deleteByAccount(String accountNo) {
        try (Connection conn = ConnUtil.getConn()) {
            // 防呆 查帳號是否存在
            AccountEntity accountEntity = accountDAO.findByAccount(conn, accountNo);

            if (accountEntity == null || accountEntity.getAccount() == null || accountEntity.getAccount().trim().isEmpty()) {
                logger.warn("刪除失敗， {} 不存在。", accountNo);
                throw new RuntimeException("刪除失敗，帳號不存在。");
            }

            if (accountDAO.deleteByAccount(conn, accountNo)) {
                // 因為本來就不應該存在的方法 所以用warn
                logger.warn("已成功刪除帳戶 account: {}", accountNo);
            } else {
                logger.warn("帳號刪除失敗");
                throw new RuntimeException("帳號刪除失敗，請確認logger，並重新嘗試");
            }

        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException(e);
        }
    }


    // 提款
    public void withdraw(String accountNo, BigDecimal amount) {
        // 防呆 不得為負
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("提款金額必須大於零");
        }

        try (Connection conn = ConnUtil.getConn()) {
            conn.setAutoCommit(false); // 關閉自動commit，確保ACID

            try {
                // 查詢帳戶並檢查餘額
                AccountEntity accountEntity = accountDAO.findByAccount(conn, accountNo);
                if (accountEntity == null) {
                    throw new RuntimeException("提款失敗，找不到該帳戶");
                }
                if (accountEntity.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("提款失敗，餘額不足");
                }

                // 上面確認完畢就執行扣款
                boolean isUpdated = accountDAO.updateBalance(conn, accountNo, amount.negate());
                if (!isUpdated) {
                    throw new RuntimeException("提款更新失敗，未更動資料");
                }

                // 計算餘額
                BigDecimal balanceAfter = accountEntity.getBalance().subtract(amount);

                // 封裝交易紀錄
                TransLogEntity log = new TransLogEntity();
                /**
                 * UUID (Universally Unique Identifier，通用唯一辨識碼)
                 * 用最直白的話來說，就是一個「號稱在整個宇宙中，永遠不會重複的隨機亂碼」。
                 * 在 Java 裡面，只要呼叫 UUID.randomUUID().toString()
                 * 就會立刻生出一串長得像這樣的 36 碼字串：
                 * 550e8400-e29b-41d4-a716-446655440000
                 * */
                log.setReferenceId(UUID.randomUUID().toString());
                log.setAmount(amount);
                log.setType("WITHDRAW");
                log.setOperationAccount(accountNo);
                log.setOtherAccount(null); // 提款沒有對象帳號
                log.setBalance(balanceAfter);
                log.setTransactionTime(LocalDateTime.now());
                log.setNote("提款");

                // 寫入流水帳
                transLogDAO.insert(conn, log);

                // 成功就commit
                conn.commit();
                logger.info("提款成功！帳戶: {}, 提款金額: {}, 剩餘餘額: {}", accountNo, amount, balanceAfter);

            } catch (Exception e) {
                conn.rollback();
                logger.error("提款失敗，執行 Rollback: {}", e.getMessage());
                throw new RuntimeException("提款處理異常", e);
            } finally {
                conn.setAutoCommit(true); // 記得失敗也要開回去auto
            }
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線異常", e);
        }
    }

    // 存款
    public void deposit(String accountNo, BigDecimal amount) {
        // 防呆
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("存款金額必須大於零");
        }

        try (Connection conn = ConnUtil.getConn()) {
            conn.setAutoCommit(false); // ACID

            try {
                AccountEntity account = accountDAO.findByAccount(conn, accountNo);

                // 老樣子 防呆
                if (account == null) {
                    throw new RuntimeException("存款失敗，帳號不存在");
                }

                // 凍結不給領
                if ("FROZEN".equals(account.getStatus())) {
                    throw new RuntimeException("存款失敗，帳戶已凍結");
                }

                // 成功就更新餘額
                boolean isUpdated = accountDAO.updateBalance(conn, accountNo, amount);
                if (!isUpdated) {
                    throw new RuntimeException("存款更新失敗");
                }

                // 計算交易後餘額
                BigDecimal balanceAfter = account.getBalance().add(amount);


                TransLogEntity log = new TransLogEntity();
                log.setReferenceId(UUID.randomUUID().toString());
                log.setAmount(amount);
                log.setType("DEPOSIT");
                log.setOperationAccount(accountNo);
                log.setOtherAccount(null);
                log.setBalance(balanceAfter);
                log.setTransactionTime(LocalDateTime.now());
                log.setNote("現金存款");

                // 寫入交易紀錄
                transLogDAO.insert(conn, log);

                conn.commit(); // 完成就commit
                logger.info("存款成功！帳戶: {}, 金額: {}, 新餘額: {}", accountNo, amount, balanceAfter);

            } catch (Exception e) {
                conn.rollback(); // 失敗就rollback
                logger.error("存款發生異常，執行 Rollback: {}", e.getMessage());
                throw new RuntimeException("存款處理失敗", e);
            } finally {
                conn.setAutoCommit(true);// 確保任何情況下結束都會開回去auto
            }
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線失敗", e);
        }
    }

    // 轉帳
    public void transfer(String fromAccNo, String toAccNo, BigDecimal amount, String note) {
        // 基本防呆
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("轉帳金額必須大於零");
        }
        if (fromAccNo.equals(toAccNo)) {
            throw new RuntimeException("轉出與轉入帳號不可相同");
        }

        try (Connection conn = ConnUtil.getConn()) {
            conn.setAutoCommit(false); // 免死金牌 資料庫很猛的

            try {
                // 檢查轉出帳戶 (From Account)
                AccountEntity fromAcc = accountDAO.findByAccount(conn, fromAccNo);
                if (fromAcc == null) throw new RuntimeException("轉出帳號不存在");
                if (fromAcc.getBalance().compareTo(amount) < 0) throw new RuntimeException("餘額不足");
                if ("FROZEN".equals(fromAcc.getStatus())) throw new RuntimeException("轉出帳戶已凍結");

                // 檢查轉入帳戶 (To Account)
                AccountEntity toAcc = accountDAO.findByAccount(conn, toAccNo);
                if (toAcc == null) throw new RuntimeException("轉入帳號不存在");
                if ("FROZEN".equals(toAcc.getStatus())) throw new RuntimeException("轉入帳戶已凍結");


                // 上面都確認沒問題就開始動錢

                // 轉出帳戶扣錢
                accountDAO.updateBalance(conn, fromAccNo, amount.negate());
                // Double check
                BigDecimal fromBalanceAfter = accountDAO.findByAccount(conn, fromAccNo).getBalance();

                // 收款帳戶加錢
                accountDAO.updateBalance(conn, toAccNo, amount);
                // Double check
                BigDecimal toBalanceAfter = accountDAO.findByAccount(conn, toAccNo).getBalance();

                // 一筆轉帳兩筆紀錄 一個帳號對應一個
                // 兩筆紀錄 ReferenceId 要一樣
                String commonRef = UUID.randomUUID().toString(); // 使用同一個 UUID 關聯這兩筆操作

                // 寫入"轉出帳戶"的轉出紀錄
                TransLogEntity fromLog = new TransLogEntity();
                fromLog.setReferenceId(commonRef);
                fromLog.setAmount(amount.negate()); // 轉出顯示負數
                fromLog.setType("TRANSFER_OUT");
                fromLog.setOperationAccount(fromAccNo);
                fromLog.setOtherAccount(toAccNo);
                fromLog.setBalance(fromBalanceAfter);
                fromLog.setTransactionTime(LocalDateTime.now());
                fromLog.setNote(note + " (轉給 " + toAccNo + ")");
                transLogDAO.insert(conn, fromLog);

                // 寫入"轉入帳戶" 的轉入紀錄
                TransLogEntity toLog = new TransLogEntity();
                toLog.setReferenceId(commonRef);
                toLog.setAmount(amount); // 轉入顯示正數
                toLog.setType("TRANSFER_IN");
                toLog.setOperationAccount(toAccNo);
                toLog.setOtherAccount(fromAccNo);
                toLog.setBalance(toBalanceAfter);
                toLog.setTransactionTime(LocalDateTime.now());
                toLog.setNote(note + " (來自 " + fromAccNo + ")");
                transLogDAO.insert(conn, toLog);

                // 大功告成 Commit
                conn.commit();
                logger.info("轉帳成功！{} ➡️ {}，金額: {}", fromAccNo, toAccNo, amount);

            } catch (Exception e) {
                conn.rollback(); // 只要有一筆 Insert 失敗或餘額不足，全部退回原始狀態
                logger.error("轉帳失敗，已執行 Rollback: {}", e.getMessage());
                throw new RuntimeException("轉帳交易失敗", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線異常", e);
        }
    }
}
