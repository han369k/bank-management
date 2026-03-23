package com.ispan.bankmanagement.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ispan.bankmanagement.account.common.exception.ResourceNotFoundException;
import com.ispan.bankmanagement.account.entity.TransLogEntity;
import com.ispan.bankmanagement.account.service.TransLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@WebServlet("/translog")
public class TransLogController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(TransLogController.class);

    private TransLogService transLogService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        try {
            transLogService = new TransLogService();
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            logger.info("TransLogController 初始化成功。");
        } catch (Exception e) {
            logger.error("TransLogController 初始化失敗！", e);
            throw new ServletException("TransLogController 初始化失敗。", e);
        }
    }

    /**
     * 處理 GET 請求：所有查詢操作。
     *
     * action=getByRef      → ?referenceId=xxx
     * action=getByAccount  → ?account=xxx
     * action=getByCustomer → ?customerId=xxx
     * action=search        → ?customerId=&account=&startDate=&endDate=&page=
     * action=count         → ?customerId=&account=&startDate=&endDate=
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        logger.info("接收到 GET 請求: {}", req.getRequestURI() +
                (req.getQueryString() != null ? "?" + req.getQueryString() : ""));

        String action = req.getParameter("action");

        try {
            switch (action == null ? "" : action) {
                case "getByRef":
                    handleGetByReferenceId(req, resp);
                    break;
                case "getByAccount":
                    handleGetByAccount(req, resp);
                    break;
                case "getByCustomer":
                    handleGetByCustomerId(req, resp);
                    break;
                case "search":
                    handleSearch(req, resp);
                    break;
                case "count":
                    handleCount(req, resp);
                    break;
                default:
                    sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                            Map.of("error", "未知的操作，請提供有效的 action 參數"));
                    break;
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    /**
     * 處理 POST 請求：刪除操作（專題展示用）。
     *
     * action=delete → body: { "referenceId": "xxx" }
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        logger.info("接收到 POST 請求: {}", req.getRequestURI());

        String action = req.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "未知的操作請求，請提供 action 參數"));
            return;
        }

        try {
            String jsonBody = readJsonBody(req);

            switch (action) {
                case "delete":
                    handleDelete(resp, jsonBody);
                    break;
                default:
                    sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                            Map.of("error", "未支援的操作: " + action));
                    break;
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    // --- GET 處理方法 ---

    private void handleGetByReferenceId(HttpServletRequest req, HttpServletResponse resp) {
        String referenceId = req.getParameter("referenceId");
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("referenceId 參數為必填");
        }
        TransLogEntity log = transLogService.getLogByReferenceId(referenceId);
        sendJson(resp, HttpServletResponse.SC_OK, log);
    }

    private void handleGetByAccount(HttpServletRequest req, HttpServletResponse resp) {
        String account = req.getParameter("account");
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("account 參數為必填");
        }
        List<TransLogEntity> logs = transLogService.getLogsByAccount(account);
        sendJson(resp, HttpServletResponse.SC_OK, logs);
    }

    private void handleGetByCustomerId(HttpServletRequest req, HttpServletResponse resp) {
        String customerId = req.getParameter("customerId");
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId 參數為必填");
        }
        List<TransLogEntity> logs = transLogService.getLogsByCustomerId(customerId);
        sendJson(resp, HttpServletResponse.SC_OK, logs);
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) {
        String account    = req.getParameter("account");    // 可選
        String customerId = req.getParameter("customerId"); // 可選
        String startStr   = req.getParameter("startDate");  // 可選，格式 yyyy-MM-dd
        String endStr     = req.getParameter("endDate");    // 可選，格式 yyyy-MM-dd
        String pageStr    = req.getParameter("page");       // 必填

        if (pageStr == null || pageStr.isBlank()) {
            throw new IllegalArgumentException("page 參數為必填");
        }

        int page;
        try {
            page = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("page 必須為整數");
        }

        LocalDate startDate = parseDate(startStr, "startDate");
        LocalDate endDate   = parseDate(endStr,   "endDate");

        List<TransLogEntity> logs =
                transLogService.searchLogs(customerId, account, startDate, endDate, page);
        sendJson(resp, HttpServletResponse.SC_OK, logs);
    }

    private void handleCount(HttpServletRequest req, HttpServletResponse resp) {
        String customerId = req.getParameter("customerId");
        String account    = req.getParameter("account");
        LocalDate startDate = parseDate(req.getParameter("startDate"), "startDate");
        LocalDate endDate   = parseDate(req.getParameter("endDate"),   "endDate");

        int total = transLogService.countLogs(customerId, account, startDate, endDate);
        sendJson(resp, HttpServletResponse.SC_OK, Map.of("total", total));
    }

    // --- POST 處理方法 ---

    private void handleDelete(HttpServletResponse resp, String jsonBody) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, String> body = objectMapper.readValue(jsonBody,
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});

        String referenceId = body.get("referenceId");
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("referenceId 欄位為必填");
        }

        transLogService.deleteLogByReferenceId(referenceId);
        sendJson(resp, HttpServletResponse.SC_OK,
                Map.of("message", "交易紀錄 " + referenceId + " 已刪除"));
    }

    // --- 輔助方法 ---
    /**
     * 解析日期字串，格式須為 ISO 8601（yyyy-MM-dd）。
     * 若為 null 或空值則回傳 null（視為不篩選）。
     * 格式錯誤則拋 IllegalArgumentException。
     */
    private LocalDate parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " 日期格式不正確，應為 yyyy-MM-dd，收到: " + dateStr);
        }
    }

    /**
     * 解析頁碼，預設為第 1 頁。
     * 若非數字則拋 IllegalArgumentException。
     */
    private int parsePage(String pageStr) {
        if (pageStr == null || pageStr.isBlank()) {
            return 1;
        }
        try {
            return Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("page 格式不正確，應為整數，收到: " + pageStr);
        }
    }

    /**
     * 從 request body 讀取 JSON 字串。
     */
    private String readJsonBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String json = sb.toString();
        if (json.isBlank()) {
            throw new IllegalArgumentException("Request body 不得為空");
        }
        return json;
    }

    /**
     * 序列化 Java 物件為 JSON 並寫入 response。
     */
    private void sendJson(HttpServletResponse resp, int status, Object data) {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(status);
        try (PrintWriter out = resp.getWriter()) {
            objectMapper.writeValue(out, data);
        } catch (IOException e) {
            logger.error("發送 JSON 回應時發生 I/O 錯誤", e);
        }
    }

    /**
     * 統一例外處理。
     * IllegalArgumentException → 400
     * ResourceNotFoundException → 404
     * 其他                      → 500
     */
    private void handleException(HttpServletResponse resp, Exception e) {
        if (e instanceof IllegalArgumentException) {
            logger.warn("請求參數不合法: {}", e.getMessage());
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", e.getMessage()));
        } else if (e instanceof ResourceNotFoundException) {
            logger.warn("請求的資源不存在: {}", e.getMessage());
            sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("error", e.getMessage()));
        } else {
            logger.error("伺服器內部發生非預期錯誤", e);
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "伺服器內部錯誤: " + e.getMessage()));
        }
    }
}