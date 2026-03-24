package com.ispan.bankmanagement.card.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.ispan.bankmanagement.card.vo.CreditCard;
import com.ispan.bankmanagement.card.vo.CreditCardTransaction;
import com.ispan.bankmanagement.card.dto.MonthlyBill;
import com.ispan.bankmanagement.card.service.CreditCardBillService;

@WebServlet("/bill")
public class BillController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private CreditCardBillService billService = new CreditCardBillService();
	
    public BillController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");
		if ("detail".equals(action)) {
			showDetail(request, response);
		} else {
			showCurrent(request, response);
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("pay".equals(action)) {
			payBill(request, response);
		} else {
		doGet(request, response);
		}
		
	}
	
	//單一帳單明細
	private void showDetail(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		String month = request.getParameter("month");
		String customerIdStr = request.getParameter("customerId");

	    if (month == null || month.isEmpty()) {
	        response.sendRedirect(request.getContextPath() + "/bill");
	        return;
	    }
	    if (customerIdStr == null || customerIdStr.isEmpty()) {
	        response.sendRedirect(request.getContextPath() + "/bill");
	        return;
	    }
	    int customerId = Integer.parseInt(customerIdStr);
	    
	    String cardIdStr = request.getParameter("cardId");
	    request.setAttribute("selectedCardId", cardIdStr);

	    List<CreditCardTransaction> details;

	    // ✅ 依卡片篩選
	    if (cardIdStr != null && !cardIdStr.isEmpty()) {
	        int cardId = Integer.parseInt(cardIdStr);
	        details = billService.getBillDetailsByMonthAndCard(month, cardId,customerId);
	    } else {
	        // ✅ 全部卡片
	        details = billService.getBillDetailsByMonth(month,customerId);
	    }

	    // ✅ 計算總金額
	    BigDecimal total = BigDecimal.ZERO;
	    for (CreditCardTransaction tx : details) {
	        total = total.add(tx.getTxnAmount());
	    }

	    // ✅ 目前先不處理已繳（簡化）
	    BigDecimal paid = BigDecimal.ZERO;
	    BigDecimal remaining = total.subtract(paid);

	    // 卡片清單（做下拉）
	    List<CreditCard> cardList = billService.getCardsByCustomerId(customerId);

	    request.setAttribute("month", month);
	    request.setAttribute("details", details);
	    request.setAttribute("cardList", cardList);
	    request.setAttribute("calculatedTotal", total);
	    request.setAttribute("calculatedRemaining", remaining);
	    request.setAttribute("customerId", customerId);

	    request.getRequestDispatcher("/WEB-INF/views/creditCard/billDetail.jsp")
	           .forward(request, response);


	}
	//帳單列表
	private void showCurrent(HttpServletRequest request,HttpServletResponse response) 
			throws ServletException, IOException {
		
		List<Integer> customerList = Arrays.asList(1, 2, 3);
	    request.setAttribute("customerList", customerList);
		
		String customerIdStr=request.getParameter("customerId");
		
		List<MonthlyBill> bills;
		if (customerIdStr != null && !customerIdStr.isEmpty()) {
	        int customerId = Integer.parseInt(customerIdStr);
	        bills = billService.getMonthlyBillsByCustomerId(customerId);
	        request.setAttribute("customerId", customerId);
	    } else {
	        bills = billService.getMonthlyBills();
	    }

	    request.setAttribute("billList", bills);
		request.getRequestDispatcher("/WEB-INF/views/creditCard/billCurrent.jsp").forward(request, response);
	}
	//付款
	private void payBill(HttpServletRequest request,HttpServletResponse response) throws IOException {
		int billId =Integer.parseInt(request.getParameter("id"));
		BigDecimal amount=new BigDecimal(request.getParameter("amount"));
		
		billService.payBill(billId, amount);
		
		response.sendRedirect(request.getContextPath()+"/bill");
	}

}
