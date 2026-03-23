package com.ispan.bankmanagement.account.service;


import com.ispan.bankmanagement.account.common.exception.AccountFrozenException;
import com.ispan.bankmanagement.account.dao.AccountDAO;
import com.ispan.bankmanagement.account.dao.TransLogDAO;
import com.ispan.bankmanagement.account.entity.AccountEntity;
import com.ispan.bankmanagement.account.entity.TransLogEntity;
import com.ispan.bankmanagement.account.common.exception.ResourceNotFoundException;
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

    // Service 層持有 DAO 層的實例，以便呼叫資料庫操作方法
    AccountDAO accountDAO = new AccountDAO();
    TransLogDAO transLogDAO = new TransLogDAO();

    // 新增帳戶
    public void createAcc(AccountEntity accountEntity) {
        // 使用 try-with-resources 語句，確保 Connection 在使用完畢後會自動關閉
        try (Connection conn = ConnUtil.getConn()) {

            // 業務邏輯：對傳入的參數進行基本驗證 (防呆)
            if (accountEntity == null || accountEntity.getAccount() == null || accountEntity.getAccount().trim().isEmpty()) {
                logger.warn("新增失敗，提供的資料格式不正確。");
                throw new IllegalArgumentException("帳戶資料與帳號不可為空。");
            }

            // 業務邏輯：檢查帳號是否已存在
            // 這裡的寫法很特別：因為 DAO 層在找不到帳號時會拋出 ResourceNotFoundException，
            // 所以我們用 try-catch 來捕捉這個「預期中的例外」。
            try {
                accountDAO.findByAccount(conn, accountEntity.getAccount());
                // 如果上面那行程式碼「沒有」拋出例外，就代表帳號已經存在了，這是不允許的。
                throw new IllegalArgumentException("帳號 " + accountEntity.getAccount() + " 已存在，無法重複新增。");
            } catch (ResourceNotFoundException e) {
                // 如果捕捉到 ResourceNotFoundException，代表查無此帳戶，這是我們期望的情況。
                // 這個 catch 區塊可以留空，或者留下一個 debug 日誌，表示帳號可用。
                logger.debug("帳號 {} 可用，繼續新增流程。", accountEntity.getAccount());
            }

            // 業務邏輯：限制帳號長度
            if (accountEntity.getAccount().trim().length() != 12) {
                throw new IllegalArgumentException("帳號格式不合法，長度應為12碼");
            }

            // 呼叫 DAO 層執行資料庫插入
            accountDAO.insert(conn, accountEntity);
            logger.info("帳戶 {} 建立成功。", accountEntity.getAccount());

        } catch (SQLException e) {
            // 如果在取得連線或執行 SQL 時發生錯誤，將其包裝成 RuntimeException 向上拋出
            throw new RuntimeException("資料庫連線或操作失敗，請稍後再試。", e);
        }
    }

    // 透過帳號查詢單筆帳戶資料
    public AccountEntity getAccountByAccount(String account) {
        try (Connection conn = ConnUtil.getConn()) {
            // 直接回傳 DAO 的查詢結果。
            // Service 層不再需要處理「找不到」的情況，因為 DAO 層會直接拋出 ResourceNotFoundException。
            return accountDAO.findByAccount(conn, account);
        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("資料庫連線或操作失敗，請稍後再試。", e);
        }
    }

    // todo: 需要改寫DAO (join custormer)
    //public AccountEntity searchAccountByCustormerName(){}

    // 查詢所有帳戶 (或根據條件)
    public List<AccountEntity> getAllAccount(AccountEntity accountEntity) {
        try (Connection conn = ConnUtil.getConn()) {
            List<AccountEntity> list = accountDAO.query(conn, accountEntity);
            logger.info("帳戶條件查詢完成，共找到 {} 筆資料。", list.size());
            return list;
        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("資料庫連線或操作失敗，請稍後再試。", e);
        }
    }

    // 更新帳戶狀態
    // todo: insert adminLog
    public void updateStatus(String accountNo, String status) {
        try (Connection conn = ConnUtil.getConn()) {
            // 直接呼叫 DAO 進行更新。
            // Service 層不再需要先檢查帳號是否存在，因為 DAO 的 updateStatus 方法在找不到帳號時會自己拋出例外。
            accountDAO.updateStatus(conn, accountNo, status);
            logger.info("狀態更新成功, account: {}, 新狀態: {}", accountNo, status);
        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("資料庫操作失敗", e);
        }
    }


    // 為了對應專題的CRUD展示而實作
    // 沒事切記不要使用這個method
    // Demo時記得要刪沒有任何交易紀錄的帳號
    public void deleteByAccount(String accountNo) {
        try (Connection conn = ConnUtil.getConn()) {
            // 同上，直接呼叫 DAO 進行刪除，依賴 DAO 處理找不到帳號的情況。
            accountDAO.deleteByAccount(conn, accountNo);
            // DAO 成功執行後，Service 層才記錄日誌。
            logger.warn("已成功刪除帳戶 account: {}", accountNo);
        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("資料庫操作失敗", e);
        }
    }


    // 提款
    public void withdraw(String accountNo, BigDecimal amount) {
        // 提款金額必須是正數
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("提款金額必須大於零");
        }

        try (Connection conn = ConnUtil.getConn()) {
            // 開啟手動交易，確保「扣款」和「寫入日誌」這兩個操作要嘛全部成功，要嘛全部失敗。
            conn.setAutoCommit(false);

            try {
                // 1. 鎖定並查詢帳戶，檢查狀態與餘額
                AccountEntity accountEntity = accountDAO.findByAccount(conn, accountNo);
                
                // 業務邏輯：凍結的帳戶不能提款
                if ("FROZEN".equals(accountEntity.getStatus())) {
                    throw new AccountFrozenException("提款失敗，帳戶已凍結");
                }
                // 業務邏輯：餘額必須足夠
                if (accountEntity.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("提款失敗，餘額不足");
                }

                // 2. 執行扣款 (傳入負數金額)
                accountDAO.updateBalance(conn, accountNo, amount.negate());

                // 3. 在 Java 記憶體中計算交易後餘額，準備寫入日誌
                BigDecimal balanceAfter = accountEntity.getBalance().subtract(amount);

                // 4. 封裝交易紀錄 (TransLog)
                TransLogEntity log = new TransLogEntity();
                log.setReferenceId(UUID.randomUUID().toString()); // 產生一個唯一的交易參考碼
                log.setAmount(amount);
                log.setType("WITHDRAW");
                log.setOperationAccount(accountNo);
                log.setOtherAccount(null); // 提款沒有對象帳號
                log.setBalance(balanceAfter);
                log.setTransactionTime(LocalDateTime.now());
                log.setNote("提款");

                // 5. 寫入交易日誌
                transLogDAO.insert(conn, log);

                // 6. 所有操作都成功，提交交易
                conn.commit();
                logger.info("提款成功！帳戶: {}, 提款金額: {}, 剩餘餘額: {}", accountNo, amount, balanceAfter);

            } catch (Exception e) {
                // 如果在 try 區塊中發生任何例外 (例如餘額不足、資料庫錯誤)，就回復所有已做的操作
                conn.rollback();
                logger.error("提款失敗，執行 Rollback: {}", e.getMessage());
                // 將原始例外包裝後向上拋出，讓 Controller 層知道交易失敗
                throw new RuntimeException("提款處理異常", e);
            } finally {
                // 無論成功或失敗，最後都要將連線恢復為自動提交模式
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線異常", e);
        }
    }

    // 存款
    public void deposit(String accountNo, BigDecimal amount) {
        // 存款金額必須是正數
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("存款金額必須大於零");
        }

        try (Connection conn = ConnUtil.getConn()) {
            conn.setAutoCommit(false); // 開啟手動交易

            try {
                // 1. 查詢帳戶是否存在、是否被凍結
                AccountEntity account = accountDAO.findByAccount(conn, accountNo);
                if ("FROZEN".equals(account.getStatus())) {
                    throw new AccountFrozenException("存款失敗，帳戶已凍結");
                }

                // 2. 執行存款 (傳入正數金額)
                accountDAO.updateBalance(conn, accountNo, amount);

                // 3. 計算交易後餘額
                BigDecimal balanceAfter = account.getBalance().add(amount);

                // 4. 封裝並寫入交易日誌
                TransLogEntity log = new TransLogEntity();
                log.setReferenceId(UUID.randomUUID().toString());
                log.setAmount(amount);
                log.setType("DEPOSIT");
                log.setOperationAccount(accountNo);
                log.setOtherAccount(null);
                log.setBalance(balanceAfter);
                log.setTransactionTime(LocalDateTime.now());
                log.setNote("現金存款");
                transLogDAO.insert(conn, log);

                // 5. 提交交易
                conn.commit();
                logger.info("存款成功！帳戶: {}, 金額: {}, 新餘額: {}", accountNo, amount, balanceAfter);

            } catch (Exception e) {
                conn.rollback();
                logger.error("存款發生異常，執行 Rollback: {}", e.getMessage());
                throw new RuntimeException("存款處理失敗", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線失敗", e);
        }
    }

    // 轉帳
    public void transfer(String fromAccNo, String toAccNo, BigDecimal amount, String note) {
        // 基本的參數驗證
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("轉帳金額必須大於零");
        }
        if (fromAccNo.equals(toAccNo)) {
            throw new IllegalArgumentException("轉出與轉入帳號不可相同");
        }

        try (Connection conn = ConnUtil.getConn()) {
            conn.setAutoCommit(false); // 開啟手動交易，轉帳是 ACID 的經典場景

            try {
                // 1. 鎖定並檢查轉出帳戶
                AccountEntity fromAcc = accountDAO.findByAccount(conn, fromAccNo);
                if (fromAcc.getBalance().compareTo(amount) < 0) throw new RuntimeException("餘額不足");
                if ("FROZEN".equals(fromAcc.getStatus())) throw new AccountFrozenException("轉出帳戶已凍結");

                // 2. 鎖定並檢查轉入帳戶
                AccountEntity toAcc = accountDAO.findByAccount(conn, toAccNo);
                if ("FROZEN".equals(toAcc.getStatus())) throw new AccountFrozenException("轉入帳戶已凍結");

                // 3. 計算更新後餘額，並執行更新
                // 這裡先在 Java 中計算好餘額，可以減少兩次不必要的資料庫查詢
                BigDecimal fromBalanceAfter = fromAcc.getBalance().subtract(amount);
                accountDAO.updateBalance(conn, fromAccNo, amount.negate());

                BigDecimal toBalanceAfter = toAcc.getBalance().add(amount);
                accountDAO.updateBalance(conn, toAccNo, amount);

                // 4. 準備兩筆交易日誌，並使用同一個 Reference ID 進行關聯
                String commonRef = UUID.randomUUID().toString();
                LocalDateTime now = LocalDateTime.now();

                // 4a. 寫入"轉出方"的日誌
                TransLogEntity fromLog = new TransLogEntity();
                fromLog.setReferenceId(commonRef);
                fromLog.setAmount(amount.negate()); // 轉出方的金額紀錄為負數
                fromLog.setType("TRANSFER_OUT");
                fromLog.setOperationAccount(fromAccNo);
                fromLog.setOtherAccount(toAccNo);
                fromLog.setBalance(fromBalanceAfter);
                fromLog.setTransactionTime(now);
                fromLog.setNote(note != null ? note : "" + " (轉給 " + toAccNo + ")");
                transLogDAO.insert(conn, fromLog);

                // 4b. 寫入"轉入方"的日誌
                TransLogEntity toLog = new TransLogEntity();
                toLog.setReferenceId(commonRef);
                toLog.setAmount(amount); // 轉入方的金額紀錄為正數
                toLog.setType("TRANSFER_IN");
                toLog.setOperationAccount(toAccNo);
                toLog.setOtherAccount(fromAccNo);
                toLog.setBalance(toBalanceAfter);
                toLog.setTransactionTime(now);
                toLog.setNote(note != null ? note : "" + " (來自 " + fromAccNo + ")");
                transLogDAO.insert(conn, toLog);

                // 5. 所有資料庫操作都成功，正式提交交易
                conn.commit();
                logger.info("轉帳成功！{} ➡️ {}，金額: {}", fromAccNo, toAccNo, amount);

            } catch (Exception e) {
                conn.rollback(); // 只要中途發生任何錯誤，就回復所有操作，確保資料一致性
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
