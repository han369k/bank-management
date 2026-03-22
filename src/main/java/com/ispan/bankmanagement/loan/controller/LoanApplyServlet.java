package com.ispan.bankmanagement.loan.controller;

import com.ispan.bankmanagement.loan.service.LoanApplyService;
import com.ispan.bankmanagement.loan.vo.LoanApplyBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/loanApply")
public class LoanApplyServlet extends HttpServlet {

    private LoanApplyService loanApplyService = new LoanApplyService();

    // ===============================
    // 🔹 GET（查詢 / 導頁）
    // ===============================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if (action == null || "list".equals(action)) {

            req.setAttribute("list",
                    loanApplyService.getLoans(null, null, null, null));

            req.getRequestDispatcher("/LoanApply.jsp")
                    .forward(req, resp);
        }
    }

    // ===============================
    // 🔹 POST（新增 / 審核 / 操作）
    // ===============================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        if (action == null) {
            resp.sendRedirect(req.getContextPath() + "/loanApply?action=list");
            return;
        }

        switch (action) {

            case "apply":
                applyLoan(req, resp);
                break;

            case "approve":
                processApproval(req, resp);
                break;

            case "confirm":
                confirm(req, resp);
                break;

            case "rejectByBank":
                rejectByBank(req, resp);
                break;

            case "rejectByCustomer":
                rejectByCustomer(req, resp);
                break;
        }
    }

    // ===============================
    // 🔹 方法區
    // ===============================

    private void applyLoan(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        LoanApplyBean loan = new LoanApplyBean();

        loan.setApplicationId(req.getParameter("applicationId"));
        loan.setCustomerId(req.getParameter("customerId"));
        loan.setApplyAmount(new BigDecimal(req.getParameter("applyAmount")));
        loan.setApplyPeriod(Integer.parseInt(req.getParameter("applyPeriod")));

        loanApplyService.applyLoan(loan);

        resp.sendRedirect(req.getContextPath() + "/loanApply?action=list");
    }

    private void processApproval(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        LoanApplyBean loan = new LoanApplyBean();

        loan.setApplicationId(req.getParameter("applicationId"));
        loan.setApprovedAmount(new BigDecimal(req.getParameter("approvedAmount")));
        loan.setApprovedRate(new BigDecimal(req.getParameter("approvedRate")));
        loan.setApprovedPeriod(Integer.parseInt(req.getParameter("approvedPeriod")));
        loan.setReviewerId(Integer.parseInt(req.getParameter("reviewerId")));

        loanApplyService.processApproval(loan);

        resp.sendRedirect(req.getContextPath() + "/loanApply?action=list");
    }

    private void confirm(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String id = req.getParameter("applicationId");

        loanApplyService.confirmApproval(id);

        resp.sendRedirect(req.getContextPath() + "/loanApply?action=list");
    }

    private void rejectByBank(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String id = req.getParameter("applicationId");
        Integer reviewerId = Integer.parseInt(req.getParameter("reviewerId"));

        loanApplyService.rejectByBank(id, reviewerId);

        resp.sendRedirect(req.getContextPath() + "/loanApply?action=list");
    }

    private void rejectByCustomer(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String id = req.getParameter("applicationId");

        loanApplyService.rejectByCustomer(id);

        resp.sendRedirect(req.getContextPath() + "/loanApply?action=list");
    }
}