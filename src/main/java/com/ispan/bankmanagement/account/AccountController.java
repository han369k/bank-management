//package com.ispan.bankmanagement.account;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.ispan.bankmanagement.account.common.exception.AccountFrozenException;
//import com.ispan.bankmanagement.account.common.exception.ResourceNotFoundException;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
///**
// * 帳戶管理的 Servlet Controller。
// * 負責接收所有與 /account 相關的 HTTP 請求，並以 JSON 格式回應。
// * 使用 action 參數來區分不同的操作。
// */
//@WebServlet("/account")
//public class AccountController extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//
//    // 每個 Servlet 都應該有自己的 Logger，用於記錄執行軌跡與錯誤
//    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
//
//    // Service 層與 JSON 處理器的實例，由 init() 方法進行初始化
//    private AccountService accountService;
//    private ObjectMapper objectMapper;
//
//    /**
//     * Servlet 生命週期方法：初始化。
//     * 此方法只會在 Servlet 實例第一次被建立時執行一次。
//     * 這裡是進行重量級物件 (如 Service、ObjectMapper) 初始化的最佳位置，以避免重複建立。
//     */
//    @Override
//    public void init() throws ServletException {
//        try {
//            // try-catch 主要是為了捕捉 accountService 初始化過程中可能發生的嚴重錯誤 (例如資料庫連線失敗)。
//            accountService = new AccountService();
//            objectMapper = new ObjectMapper();
//            // 註冊 Java 8 Time 模組，讓 Jackson 能夠正確序列化/反序列化 LocalDateTime
//            objectMapper.registerModule(new JavaTimeModule());
//            // 禁用將日期寫為時間戳的特性，確保日期以 ISO-8601 字串格式輸出 (例如 "2023-11-20T10:00:00")
//            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            logger.info("AccountController 已成功初始化 AccountService 和 ObjectMapper。");
//        } catch (Exception e) {
//            logger.error("AccountController 初始化失敗！", e);
//            throw new ServletException("AccountController 初始化失敗，無法連接後端服務。", e);
//        }
//    }
//
//    /**
//     * 處理 GET 請求：所有查詢操作。
//     *
//     * action=get     → ?account=xxx
//     * action=getAll  → (無參數)
//     */
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.setCharacterEncoding("UTF-8");
//        logger.info("接收到 GET 請求: {}", req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
//        String action = req.getParameter("action");
//
//        try {
//            switch (action == null ? "getAll" : action) {
//                case "get":
//                    handleGetAccount(req, resp);
//                    break;
//                case "getAll":
//                default:
//                    handleGetAllAccounts(req,resp);
//                    break;
//            }
//        } catch (Exception e) {
//            handleException(resp, e);
//        }
//    }
//
//    /**
//     * 處理 POST 請求：所有新增、修改、刪除、交易等操作。
//     *
//     * action=create       → body: { "account": "...", "customerId": "...", "balance": ... }
//     * action=updateStatus → body: { "account": "...", "status": "..." }
//     * action=delete       → body: { "account": "..." }
//     * action=deposit      → body: { "account": "...", "amount": "..." }
//     * action=withdraw     → body: { "account": "...", "amount": "..." }
//     * action=transfer     → body: { "fromAccount": "...", "toAccount": "...", "amount": "..." }
//     */
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        logger.info("接收到 POST 請求: {}", req.getRequestURI());
//        req.setCharacterEncoding("UTF-8");
//        String action = req.getParameter("action");
//
//        if (action == null || action.trim().isEmpty()) {
//            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "未知的操作請求，請提供 action 參數"));
//            return;
//        }
//
//        try {
//            String jsonBody = readJsonBody(req);
//
//            switch (action) {
//                case "create":
//                    handleCreateAccount(resp, jsonBody);
//                    break;
//                case "updateStatus":
//                    handleUpdateStatus(resp, jsonBody);
//                    break;
//                case "delete":
//                    handleDeleteAccount(resp, jsonBody);
//                    break;
//                case "deposit":
//                case "withdraw":
//                case "transfer":
//                    handleTransaction(resp, action, jsonBody);
//                    break;
//                default:
//                    sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "未支援的操作: " + action));
//                    break;
//            }
//        } catch (Exception e) {
//            handleException(resp, e);
//        }
//    }
//
//    // --- GET 請求的處理方法 ---
//
//    private void handleGetAccount(HttpServletRequest req, HttpServletResponse resp) {
//        String accountNo = req.getParameter("account");
//        if (accountNo == null || accountNo.trim().isEmpty()) {
//            throw new IllegalArgumentException("查詢失敗，請提供 account 參數");
//        }
//        AccountEntity account = accountService.getAccountByAccount(accountNo);
//        sendJson(resp, HttpServletResponse.SC_OK, account);
//    }
//
//    /**
//     * 處理取得所有帳戶 (支援動態條件查詢)
//     */
//    private void handleGetAllAccounts(HttpServletRequest req, HttpServletResponse resp) {
//        // 1. 建立一個 Entity 來裝載前端傳來的查詢條件
//        AccountEntity condition = new AccountEntity();
//        condition.setAccount(req.getParameter("account"));
//        condition.setCustomerId(req.getParameter("customerId"));
//        condition.setType(req.getParameter("type"));
//        condition.setStatus(req.getParameter("status"));
//
//        // 2. 將裝滿條件的 condition 傳給 Service
//        List<AccountEntity> accounts = accountService.getAllAccount(condition);
//        sendJson(resp, HttpServletResponse.SC_OK, accounts);
//    }
//
//    // --- POST 請求的處理方法 ---
//
//    private void handleCreateAccount(HttpServletResponse resp, String jsonBody) throws IOException {
//        AccountEntity accountEntity = objectMapper.readValue(jsonBody, AccountEntity.class);
//
//        // 在呼叫 Service 前，對從 JSON 來的資料進行基本驗證
//        if (accountEntity.getAccount() == null || accountEntity.getAccount().isBlank()) {
//            throw new IllegalArgumentException("account 欄位為必填");
//        }
//        if (accountEntity.getCustomerId() == null || accountEntity.getCustomerId().isBlank()) {
//            throw new IllegalArgumentException("customerId 欄位為必填");
//        }
//        if (accountEntity.getBalance() == null) {
//            throw new IllegalArgumentException("balance 欄位為必填");
//        }
//
//        accountService.createAcc(accountEntity);
//        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "帳戶 " + accountEntity.getAccount() + " 建立成功"));
//    }
//
//    private void handleUpdateStatus(HttpServletResponse resp, String jsonBody) throws IOException {
//        Map<String, String> body = objectMapper.readValue(jsonBody, new TypeReference<Map<String, String>>() {});
//        String accountNo = body.get("account");
//        String status = body.get("status");
//
//        // 在呼叫 Service 前進行參數驗證
//        if (accountNo == null || accountNo.isBlank()) {
//            throw new IllegalArgumentException("account 欄位為必填");
//        }
//        if (status == null || status.isBlank()) {
//            throw new IllegalArgumentException("status 欄位為必填");
//        }
//
//        accountService.updateStatus(accountNo, status);
//        sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "帳戶 " + accountNo + " 狀態更新成功"));
//    }
//
//    private void handleDeleteAccount(HttpServletResponse resp, String jsonBody) throws IOException {
//        Map<String, String> body = objectMapper.readValue(jsonBody, new TypeReference<Map<String, String>>() {});
//        String accountNo = body.get("account");
//
//        // 在呼叫 Service 前進行參數驗證
//        if (accountNo == null || accountNo.isBlank()) {
//            throw new IllegalArgumentException("account 欄位為必填");
//        }
//
//        accountService.deleteByAccount(accountNo);
//        sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "帳戶 " + accountNo + " 刪除成功"));
//    }
//
//    private void handleTransaction(HttpServletResponse resp, String action, String jsonBody) throws IOException {
//        Map<String, String> body = objectMapper.readValue(jsonBody, new TypeReference<Map<String, String>>() {});
//
//        String accountNo = body.get("account");
//        String fromAccount = body.get("fromAccount");
//        String toAccount = body.get("toAccount");
//        String note = body.get("note");
//        BigDecimal amount;
//
//        String amountStr = body.get("amount");
//        if (amountStr == null || amountStr.isBlank()) {
//            throw new IllegalArgumentException("amount 欄位為必填");
//        }
//
//        try {
//            amount = new BigDecimal(amountStr);
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("金額格式不正確");
//        }
//
//        // 加上金額的業務邏輯驗證
//        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("金額必須大於零");
//        }
//
//        // 對不同 action 進行參數驗證
//        if ("transfer".equals(action)) {
//            if (fromAccount == null || fromAccount.isBlank()) {
//                throw new IllegalArgumentException("fromAccount 欄位為必填");
//            }
//            if (toAccount == null || toAccount.isBlank()) {
//                throw new IllegalArgumentException("toAccount 欄位為必填");
//            }
//        } else { // deposit or withdraw
//            if (accountNo == null || accountNo.isBlank()) {
//                throw new IllegalArgumentException("account 欄位為必填");
//            }
//        }
//
//        switch (action) {
//            case "deposit":
//                accountService.deposit(accountNo, amount);
//                break;
//            case "withdraw":
//                accountService.withdraw(accountNo, amount);
//                break;
//            case "transfer":
//                accountService.transfer(fromAccount, toAccount, amount, note);
//                break;
//            default:
//                throw new IllegalArgumentException("未支援的交易類型: " + action);
//        }
//        sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "交易 " + action + " 成功"));
//    }
//
//    // --- 統一的輔助方法 ---
//
//    /**
//     * 將 Java 物件序列化為 JSON 並發送給前端。
//     * @param resp   HttpServletResponse 物件
//     * @param status HTTP 狀態碼
//     * @param data   要序列化的 Java 物件
//     */
//    private void sendJson(HttpServletResponse resp, int status, Object data) {
//        resp.setContentType("application/json;charset=UTF-8");
//        resp.setStatus(status);
//        try (PrintWriter out = resp.getWriter()) {
//            objectMapper.writeValue(out, data);
//        } catch (IOException e) {
//            logger.error("發送 JSON 回應時發生 I/O 錯誤", e);
//        }
//    }
//
//    /**
//     * 統一處理所有 Service 層與其他未預期例外。
//     * IllegalArgumentException → 400
//     * ResourceNotFoundException → 404
//     * 其他                      → 500
//     */
//    private void handleException(HttpServletResponse resp, Exception e) {
//        if (e instanceof IllegalArgumentException) {
//            logger.warn("請求參數不合法: {}", e.getMessage());
//            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", e.getMessage()));
//        } else if (e instanceof ResourceNotFoundException) {
//            logger.warn("請求的資源不存在: {}", e.getMessage());
//            sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", e.getMessage()));
//        } else if (e instanceof AccountFrozenException) {
//            logger.warn("帳戶凍結操作被拒絕: {}", e.getMessage());
//            sendJson(resp, HttpServletResponse.SC_FORBIDDEN, Map.of("error", e.getMessage()));
//        }else {
//            logger.error("伺服器內部發生非預期錯誤", e);
//            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("error", "伺服器內部錯誤: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * 從請求中讀取 JSON body 並以字串形式回傳。
//     * @param req HttpServletRequest 物件
//     * @return JSON body 字串
//     * @throws IOException 如果讀取時發生錯誤
//     */
//    private String readJsonBody(HttpServletRequest req) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        String line;
//        try (BufferedReader reader = req.getReader()) {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//        }
//        String json = sb.toString();
//        if (json.isBlank()) {
//            throw new IllegalArgumentException("Request body 不得為空");
//        }
//        return json;
//    }
//}
