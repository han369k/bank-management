package com.ispan.bankmanagement.loan.controller;

import com.ispan.bankmanagement.loan.dao.LoanApplyDao;
import com.ispan.bankmanagement.loan.service.LoanApplyService;
import com.ispan.bankmanagement.loan.vo.LoanApplyBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@WebServlet("/loanApply")
public class LoanApplyServlet extends HttpServlet {

    private LoanApplyDao loanDao = new LoanApplyDao();
    private LoanApplyService loanService = new LoanApplyService();

    // ===============================
    // 📌 查詢列表（後台頁面）
    // ===============================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<LoanApplyBean> list = loanDao.getAll();

        req.setAttribute("list", list);

        // 👉 對應你的 JSP 檔名
        req.getRequestDispatcher("/loanApply.jsp").forward(req, resp);
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

                default:
                    insertLoan(req, resp);
                    return;
            }

            // 👉 操作完成後回列表
            resp.sendRedirect("loanApply");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // ===============================
    // 🆕 新增貸款申請
    // ===============================
    private void insertLoan(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String customerId = req.getParameter("customerId");
        String applyType = req.getParameter("applyType");
        String amountStr = req.getParameter("applyAmount");
        String periodStr = req.getParameter("applyPeriod");

        Long applyAmount = (amountStr == null || amountStr.isEmpty())
                ? null
                : Long.parseLong(amountStr);

        Integer applyPeriod = (periodStr == null || periodStr.isEmpty())
                ? null
                : Integer.parseInt(periodStr);

        BigDecimal rate = loanService.calculateRate(applyType, applyPeriod);

        LoanApplyBean loan = new LoanApplyBean();
        loan.setApplicationId(UUID.randomUUID().toString());
        loan.setCustomerId(customerId);
        loan.setApplyType(applyType);
        loan.setApplyAmount(applyAmount);
        loan.setApplyPeriod(applyPeriod);
        loan.setRate(rate);

        loanDao.insert(loan);

        resp.setContentType("application/json;charset=UTF-8");

        String json = String.format(
                "{\"status\":\"success\",\"rate\":%s}",
                rate.toString()
        );

        resp.getWriter().write(json);
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
                null,
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
                null,
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