package com.ispan.bankmanagement.account.service;

import com.ispan.bankmanagement.account.dao.TransLogDAO;
import com.ispan.bankmanagement.account.entity.TransLogEntity;
import com.ispan.bankmanagement.util.ConnUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransLogService {

    private static final Logger logger = LoggerFactory.getLogger(TransLogService.class);
    private final TransLogDAO transLogDAO = new TransLogDAO();

    /**
     * 透過 Reference ID 查詢單筆交易紀錄。
     * Reference ID 是由 UUID 生成的唯一識別碼，理論上只會查到一筆。
     * @param referenceId 交易參考編號
     * @return 找到的交易紀錄實體，如果找不到則拋出 RuntimeException
     */
    public TransLogEntity getLogByReferenceId(String referenceId) {
        try (Connection conn = ConnUtil.getConn()) {
            // 檢查傳入的參數
            if (referenceId == null || referenceId.trim().isEmpty()) {
                throw new IllegalArgumentException("Reference ID 不可為空");
            }

            TransLogEntity log = transLogDAO.findByReferenceId(conn, referenceId);

            if (log == null) {
                logger.warn("查詢失敗，找不到 Reference ID 為 {} 的交易紀錄", referenceId);
                throw new RuntimeException("查無此交易紀錄");
            }

            logger.info("成功查詢到交易紀錄, Reference ID: {}", referenceId);
            return log;

        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("查詢交易紀錄時發生資料庫連線錯誤", e);
        }
    }

    /**
     * 透過 Customer ID 查詢其名下所有帳戶的交易紀錄。
     * @param customerId
     * @return 該客戶的所有交易紀錄列表，如果沒有則回傳空List
     */
    public List<TransLogEntity> getLogsByCustomerId(String customerId) {
        try (Connection conn = ConnUtil.getConn()) {
            if (customerId == null || customerId.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer ID 不可為空");
            }

            List<TransLogEntity> logs = transLogDAO.findByCustomerId(conn, customerId);
            logger.info("為 Customer ID: {} 查詢到 {} 筆交易紀錄", customerId, logs.size());
            return logs;

        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("查詢交易紀錄時發生資料庫連線錯誤", e);
        }
    }

    /**
     * 透過帳號查詢該帳戶的所有交易紀錄。
     * @param account 銀行帳號
     * @return 該帳戶的所有交易紀錄列表，如果沒有則回傳空列表
     */
    public List<TransLogEntity> getLogsByAccount(String account) {
        try (Connection conn = ConnUtil.getConn()) {
            if (account == null || account.trim().isEmpty()) {
                throw new IllegalArgumentException("帳號不可為空");
            }

            List<TransLogEntity> logs = transLogDAO.findByOperationAccount(conn, account);
            logger.info("為帳號: {} 查詢到 {} 筆交易紀錄", account, logs.size());
            return logs;

        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("查詢交易紀錄時發生資料庫連線錯誤", e);
        }
    }

    /**
     * (因應專題而生的刪除功能)
     * 透過 Reference ID 刪除交易紀錄。
     * 實務上絕對不允許刪除交易紀錄，此方法僅為專題展示用。
     * @param referenceId 要刪除的交易參考編號
     */
    public void deleteLogByReferenceId(String referenceId) {
        try (Connection conn = ConnUtil.getConn()) {
            if (referenceId == null || referenceId.trim().isEmpty()) {
                throw new IllegalArgumentException("Reference ID 不可為空");
            }

            // 執行刪除前，先確認紀錄是否存在
            if (transLogDAO.findByReferenceId(conn, referenceId) == null) {
                logger.warn("刪除失敗，找不到 Reference ID 為 {} 的交易紀錄", referenceId);
                throw new RuntimeException("刪除失敗，查無此交易紀錄");
            }

            if (transLogDAO.deleteByReferenceId(conn, referenceId)) {
                logger.warn("警告：已成功透過 Reference ID 刪除交易紀錄: {}", referenceId);
            } else {
                // 正常情況下，如果上面 findByReferenceId 找得到，這裡應該不會是 false
                logger.error("刪除交易紀錄失敗，但紀錄存在，可能發生併發問題, Reference ID: {}", referenceId);
                throw new RuntimeException("刪除失敗，請檢查日誌");
            }

        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("刪除交易紀錄時發生資料庫連線錯誤", e);
        }
    }
}
