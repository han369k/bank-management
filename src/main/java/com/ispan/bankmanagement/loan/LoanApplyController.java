package com.ispan.bankmanagement.loan;
import com.ispan.bankmanagement.common.util.ConnUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/LoanApply")
public class LoanApplyController extends HttpServlet {

    private LoanApplyDao loanDao = new LoanApplyDao();
    private LoanApplyService loanService = new LoanApplyService();
    private void createCustomerIfNotExist(String customerId) {

        String checkSql = "SELECT COUNT(*) FROM CUSTOMER WHERE customer_id = ?";
        String insertSql = "INSERT INTO CUSTOMER (customer_id, name, created_at, status) VALUES (?, ?, SYSDATETIME(), 'ACTIVE')";

        try (Connection conn = ConnUtil.getConn()) {

            // 🔍 檢查是否存在
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, customerId);
                ResultSet rs = ps.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    return; // 已存在
                }
            }

            // ➕ 不存在 → 自動新增
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, customerId);
                ps.setString(2, "測試用戶_" + customerId);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("建立測試客戶失敗", e);
        }
    }

    // ===============================
    // 📌 查詢列表（後台頁面）
    // ===============================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String status = req.getParameter("status");
        String minAmountStr = req.getParameter("minAmount");
        String maxAmountStr = req.getParameter("maxAmount");

        Long minAmount = (minAmountStr != null && !minAmountStr.isEmpty()) ? Long.parseLong(minAmountStr) : null;
        Long maxAmount = (maxAmountStr != null && !maxAmountStr.isEmpty()) ? Long.parseLong(maxAmountStr) : null;

        List<LoanApplyBean> list;

        // 有任何篩選條件 → 用 getByStatus
        boolean hasFilter = (status != null && !status.isEmpty()) || minAmount != null || maxAmount != null;

        if (hasFilter) {
            list = loanDao.getByStatus(
                    (status != null && !status.isEmpty())
                            ? List.of(LoanApplyDao.LoanStatus.valueOf(status))
                            : null,
                    minAmount, maxAmount
            );
        } else {
            list = loanDao.getAll();
        }

        req.setAttribute("list", list);

        // 👉 對應你的 JSP 檔名
        req.getRequestDispatcher("/view/loan-loanapply.jsp").forward(req, resp);
    }

    // ===============================
    // 📌 所有操作入口
    // ===============================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");

        try {

            if (action == null || action.isEmpty()) {
                insertLoan(req, resp); // 預設：新增申請
                return;
            }

            switch (action) {

                case "approve":
                    approve(req);
                    break;

                case "approveDirect":
                    approveDirect(req);
                    break;

                case "rejectByBank":
                    rejectByBank(req);
                    break;

                case "confirm":
                    confirm(req);
                    break;

                case "rejectByCustomer":
                    rejectByCustomer(req);
                    break;

                case "deleteRejected":
                    loanDao.deleteByStatus(LoanApplyDao.LoanStatus.REJECTED);
                    break;

                default:
                    insertLoan(req, resp);
                    return;
            }

            // 👉 操作完成後回列表（保留篩選狀態）
            String filterStatus = req.getParameter("filterStatus");
            if (filterStatus != null && !filterStatus.isEmpty()) {
                resp.sendRedirect("LoanApply?status=" + filterStatus);
            } else {
                resp.sendRedirect("LoanApply");
            }

        } catch (Exception e) {
            e.printStackTrace(); // ← 印到 Tomcat console 方便除錯
            throw new ServletException(e);
        }
    }

    // ===============================
    // 🆕 新增貸款申請
    // ===============================
    private void insertLoan(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain;charset=UTF-8");

        try {
            String customerId = req.getParameter("customerId");
            String applyType = req.getParameter("applyType");
            String amountStr = req.getParameter("applyAmount");
            String periodStr = req.getParameter("applyPeriod");

            System.out.println("=== 收到貸款申請 ===");
            System.out.println("customerId=" + customerId);
            System.out.println("applyType=" + applyType);
            System.out.println("applyAmount=" + amountStr);
            System.out.println("applyPeriod=" + periodStr);

            // =====================
            // ✅ 防呆：金額
            // =====================
            if (amountStr == null || amountStr.isEmpty() || Long.parseLong(amountStr) <= 0) {
                resp.setStatus(400);
                resp.getWriter().write("錯誤：貸款金額必須大於0");
                return;
            }

            if (periodStr == null || periodStr.isEmpty()) {
                resp.setStatus(400);
                resp.getWriter().write("錯誤：請選擇期數");
                return;
            }

            Long applyAmount = Long.parseLong(amountStr);
            Integer applyPeriod = Integer.parseInt(periodStr);

            // =====================
            // ⭐ 跳過 CUSTOMER 檢查，直接寫入 LOAN_APPLICATION
            // =====================
            // createCustomerIfNotExist(customerId);

            BigDecimal rate = loanService.calculateRate(applyType, applyPeriod);

            // ⭐ 有規則的 application_id：LA + yyyyMMddHHmmss + 4碼隨機數 = 剛好 20 字元
            String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String randomSuffix = String.format("%04d", (int)(Math.random() * 10000));
            String applicationId = "LA" + timeStr + randomSuffix;

            LoanApplyBean loan = new LoanApplyBean();
            loan.setApplicationId(applicationId);
            loan.setCustomerId(customerId);
            loan.setApplyType(applyType);
            loan.setApplyAmount(applyAmount);
            loan.setApplyPeriod(applyPeriod);
            loan.setRate(rate);

            System.out.println("準備寫入 DB，applicationId=" + loan.getApplicationId());

            loanDao.insert(loan);

            System.out.println("✅ 寫入 DB 成功！");

            resp.setStatus(200);
            resp.getWriter().write("success");

        } catch (Exception e) {
            e.printStackTrace(); // ← 印到 Tomcat console
            resp.setStatus(500);
            resp.getWriter().write("伺服器錯誤：" + e.getMessage());
        }
    }

    // ===============================
    // 🟦 送客戶確認
    // ===============================
    private void approve(HttpServletRequest req) {

        LoanApplyBean loan = new LoanApplyBean();

        loan.setApplicationId(req.getParameter("applicationId"));
        loan.setApprovedAmount(Long.parseLong(req.getParameter("approvedAmount")));
        loan.setApprovedPeriod(Integer.parseInt(req.getParameter("approvedPeriod")));
        loan.setReviewerId(Integer.parseInt(req.getParameter("reviewerId")));

        // 👉 重新計算利率（用核准條件）
        BigDecimal rate = loanService.calculateRate(
                req.getParameter("applyType"),
                loan.getApprovedPeriod()
        );
        loan.setApprovedRate(rate);

        loanDao.submitForConfirm(loan);
    }

    // ===============================
    // 🟩 直接核准
    // ===============================
    private void approveDirect(HttpServletRequest req) {

        LoanApplyBean loan = new LoanApplyBean();

        loan.setApplicationId(req.getParameter("applicationId"));
        loan.setApprovedAmount(Long.parseLong(req.getParameter("approvedAmount")));
        loan.setApprovedPeriod(Integer.parseInt(req.getParameter("approvedPeriod")));
        loan.setReviewerId(Integer.parseInt(req.getParameter("reviewerId")));

        BigDecimal rate = loanService.calculateRate(
                req.getParameter("applyType"),
                loan.getApprovedPeriod()
        );
        loan.setApprovedRate(rate);

        loanDao.approveDirectly(loan);
    }

    // ===============================
    // 🟥 銀行拒絕
    // ===============================
    private void rejectByBank(HttpServletRequest req) {

        String applicationId = req.getParameter("applicationId");
        Integer reviewerId = Integer.parseInt(req.getParameter("reviewerId"));

        loanDao.rejectByBank(applicationId, reviewerId);
    }

    // ===============================
    // 🟨 客戶同意
    // ===============================
    private void confirm(HttpServletRequest req) {

        String applicationId = req.getParameter("applicationId");

        loanDao.confirmApproval(applicationId);
    }

    // ===============================
    // 🟪 客戶拒絕
    // ===============================
    private void rejectByCustomer(HttpServletRequest req) {

        String applicationId = req.getParameter("applicationId");

        loanDao.rejectByCustomer(applicationId);
    }
}
