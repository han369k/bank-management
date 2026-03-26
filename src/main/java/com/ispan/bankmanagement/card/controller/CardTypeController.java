package com.ispan.bankmanagement.card.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.ispan.bankmanagement.card.service.CardTypeService;

@WebServlet("/cardType")
public class CardTypeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private CardTypeService cardTypeService = new CardTypeService();
	
    public CardTypeController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null || "list".equals(action)) {
            request.setAttribute("cardTypeList", cardTypeService.getAllCardTypes());
            request.getRequestDispatcher("/WEB-INF/views/creditCard/cardTypeList.jsp")
                   .forward(request, response);
            return;
        }
		response.sendRedirect(request.getContextPath() + "/cardType?action=list");
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
