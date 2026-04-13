//package com.ispan.bankmanagement.account;
//
//import com.ispan.bankmanagement.common.util.ConnUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//
//public class TransLogService {
//
//    private static final Logger logger = LoggerFactory.getLogger(TransLogService.class);
//    private final TransLogDAO transLogDAO = new TransLogDAO();
//
//    /**
//     * 透過 Reference ID 查詢單筆交易紀錄。
//     * Reference ID 是由 UUID 生成的唯一識別碼，理論上只會查到一筆。
//     * @param referenceId 交易參考編號
//     * @return 找到的交易紀錄實體
//     * @throws ResourceNotFoundException 如果找不到對應的紀錄
//     */
//    public TransLogEntity getLogByReferenceId(String referenceId) {
//        try (Connection conn = ConnUtil.getConn()) {
//            if (referenceId == null || referenceId.trim().isEmpty()) {
//                throw new IllegalArgumentException("Reference ID 不可為空");
//            }
//            // 【修正 3】簡化邏輯，直接回傳 DAO 的結果，讓例外自然拋出
//            return transLogDAO.findByReferenceId(conn, referenceId);
//        } catch (SQLException e) {
//            logger.error("服務層在取得資料庫連線時發生錯誤", e);
//            throw new RuntimeException("查詢交易紀錄時發生資料庫連線錯誤", e);
//        }
//    }
//
//    /**
//     * 透過 Customer ID 查詢其名下所有帳戶的交易紀錄。
//     * @param customerId
//     * @return 該客戶的所有交易紀錄列表，如果沒有則回傳空List
//     */
//    public List<TransLogEntity> getLogsByCustomerId(String customerId) {
//        try (Connection conn = ConnUtil.getConn()) {
//            if (customerId == null || customerId.trim().isEmpty()) {
//                throw new IllegalArgumentException("Customer ID 不可為空");
//            }
//            List<TransLogEntity> logs = transLogDAO.findByCustomerId(conn, customerId);
//            logger.info("為 Customer ID: {} 查詢到 {} 筆交易紀錄", customerId, logs.size());
//            return logs;
//        } catch (SQLException e) {
//            logger.error("服務層在取得資料庫連線時發生錯誤", e);
//            throw new RuntimeException("查詢交易紀錄時發生資料庫連線錯誤", e);
//        }
//    }
//
//    /**
//     * 透過帳號查詢該帳戶的所有交易紀錄。
//     * @param account 銀行帳號
//     * @return 該帳戶的所有交易紀錄列表，如果沒有則回傳空列表
//     */
//    public List<TransLogEntity> getLogsByAccount(String account) {
//        try (Connection conn = ConnUtil.getConn()) {
//            if (account == null || account.trim().isEmpty()) {
//                throw new IllegalArgumentException("帳號不可為空");
//            }
//            List<TransLogEntity> logs = transLogDAO.findByOperationAccount(conn, account);
//            logger.info("為帳號: {} 查詢到 {} 筆交易紀錄", account, logs.size());
//            return logs;
//        } catch (SQLException e) {
//            logger.error("服務層在取得資料庫連線時發生錯誤", e);
//            throw new RuntimeException("查詢交易紀錄時發生資料庫連線錯誤", e);
//        }
//    }
//
//    /**
//     * (因應專題而生的刪除功能)
//     * 透過 Reference ID 刪除交易紀錄。
//     * 實務上絕對不允許刪除交易紀錄，此方法僅為專題展示用。
//     * @param referenceId 要刪除的交易參考編號
//     */
//    public void deleteLogByReferenceId(String referenceId) {
//        try (Connection conn = ConnUtil.getConn()) {
//            if (referenceId == null || referenceId.trim().isEmpty()) {
//                throw new IllegalArgumentException("Reference ID 不可為空");
//            }
//            // 【修正 4】簡化邏輯，先查詢確認存在，若不存在 DAO 會拋例外
//            transLogDAO.findByReferenceId(conn, referenceId);
//            // 確認存在後再執行刪除
//            transLogDAO.deleteByReferenceId(conn, referenceId);
//            logger.warn("警告：已成功透過 Reference ID 刪除交易紀錄: {}", referenceId);
//        } catch (SQLException e) {
//            logger.error("服務層在取得資料庫連線時發生錯誤", e);
//            throw new RuntimeException("刪除交易紀錄時發生資料庫連線錯誤", e);
//        }
//    }
//
//    /**
//     * 動態條件查詢交易紀錄 (支援分頁)。
//     * 這是推薦使用的主要查詢方法，可以組合不同條件。
//     *
//     * @param customerId (可選) 客戶身分證字號
//     * @param account    (可選) 銀行帳號
//     * @param startDate  (可選) 查詢區間的開始日期 (包含當天)
//     * @param endDate    (可選) 查詢區間的結束日期 (包含當天)
//     * @param page       頁碼，從 1 開始。
//     * @return 符合條件的交易紀錄列表 (單頁最多50筆)
//     */
//    public List<TransLogEntity> searchLogs(String customerId, String account, LocalDate startDate, LocalDate endDate, int page) {
//        // 【修正 6】頁碼小於 1 時拋出例外
//        if (page < 1) {
//            throw new IllegalArgumentException("頁碼必須大於等於 1");
//        }
//        // 【修正 5】使用抽離的私有方法驗證日期
//        endDate = validateEndDate(startDate, endDate);
//
//        try (Connection conn = ConnUtil.getConn()) {
//            List<TransLogEntity> logs = transLogDAO.query(conn, customerId, account, startDate, endDate, page);
//            logger.info("交易紀錄動態查詢完成，條件: [customerId={}, account={}, startDate={}, endDate={}, page={}]，共取得 {} 筆資料",
//                    customerId, account, startDate, endDate, page, logs.size());
//            return logs;
//        } catch (SQLException e) {
//            logger.error("服務層在取得資料庫連線時發生錯誤", e);
//            throw new RuntimeException("查詢交易紀錄時發生資料庫連線錯誤", e);
//        }
//    }
//
//    /**
//     * 計算符合動態條件的交易紀錄總筆數。
//     * 這個方法與 searchLogs 搭配使用，用來給前端計算總頁數。
//     *
//     * @param customerId (可選) 客戶身分證字號
//     * @param account    (可選) 銀行帳號
//     * @param startDate  (可選) 查詢區間的開始日期 (包含當天)
//     * @param endDate    (可選) 查詢區間的結束日期 (包含當天)
//     * @return 符合條件的總筆數
//     */
//    public int countLogs(String customerId, String account, LocalDate startDate, LocalDate endDate) {
//        // 【修正 5】使用抽離的私有方法驗證日期
//        endDate = validateEndDate(startDate, endDate);
//
//        try (Connection conn = ConnUtil.getConn()) {
//            int total = transLogDAO.count(conn, customerId, account, startDate, endDate);
//            logger.info("交易紀錄總筆數計算完成，條件: [customerId={}, account={}, startDate={}, endDate={}]，共 {} 筆",
//                    customerId, account, startDate, endDate, total);
//            return total;
//        } catch (SQLException e) {
//            logger.error("服務層在取得資料庫連線時發生錯誤", e);
//            throw new RuntimeException("計算交易紀錄總筆數時發生資料庫連線錯誤", e);
//        }
//    }
//
//    /**
//     * 【修正 5】抽離的日期驗證私有方法
//     * 驗證結束日期是否在開始日期之前。
//     * @return 如果日期合法，回傳原始的 endDate；否則回傳 null。
//     */
//    private LocalDate validateEndDate(LocalDate startDate, LocalDate endDate) {
//        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
//            logger.warn("查詢日期區間不合法，結束日期 {} 在開始日期 {} 之前", endDate, startDate);
//            return null;
//        }
//        return endDate;
//    }
//}
