package com.ispan.bankmanagement.creditcard;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.ispan.bankmanagement.creditcard.Enum.CardStatus;

@WebServlet("/card")
public class CardController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private CardService cardService = new CardService();

	public CardController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");

		// 1. 列表頁轉發路徑修正
		if (action == null || action.equals("list")) {
			List<CreditCard> cardList = cardService.getAllCards();
			request.setAttribute("cardList", cardList);
			// 👉 改成去外面找 view/card-list.jsp
			RequestDispatcher rd = request.getRequestDispatcher("/view/card-list.jsp");
			rd.forward(request, response);
			return;
		}

		// 2. 表單頁轉發路徑修正
		if (action.equals("new")) {
			// 👉 改成去外面找 view/card-form.jsp
			RequestDispatcher rd = request.getRequestDispatcher("/view/card-form.jsp");
			rd.forward(request, response);
			return;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");

		if ("insert".equals(action)) {
			int customerId = Integer.parseInt(request.getParameter("customerId"));
			int cardTypeId = Integer.parseInt(request.getParameter("cardTypeId"));
			String cardNumber = request.getParameter("cardNumber");
			String expiryDate = request.getParameter("expiryDate");
			String cardStatus = request.getParameter("cardStatus");

			CreditCard card = new CreditCard();
			card.setCustomerId(customerId);
			card.setCardTypeId(cardTypeId);
			card.setCardNumber(cardNumber);
			card.setExpiryDate(java.sql.Date.valueOf(expiryDate));
			//"1"→轉int→Enum→存進物件
			card.setStatus(CardStatus.valueOf(cardStatus));

			cardService.addCard(card);

			response.sendRedirect(request.getContextPath() + "/card?action=list");
			return;
		}

		//更新status
		if ("updateStatus".equals(action)) {
			int cardId = Integer.parseInt(request.getParameter("cardId"));
			String status = request.getParameter("cardStatus");

			cardService.updateStatus(cardId, CardStatus.valueOf(status));

			request.setAttribute("msg", "Updated success.");

			List<CreditCard> cardList = cardService.getAllCards();
			request.setAttribute("cardList", cardList);

			// 3. 狀態更新後的列表頁轉發路徑修正
			// 👉 改成去外面找 view/card-list.jsp
			request.getRequestDispatcher("/view/card-list.jsp").forward(request, response);
			return;
		}

		//刪除卡片
		if ("delete".equals(action)) {
			int cardId = Integer.parseInt(request.getParameter("cardId"));

			cardService.deleteCard(cardId);

			response.sendRedirect(request.getContextPath() + "/card?action=list");
			return;
		}

		//更新卡片
		if ("update".equals(action)) {
			int cardId = Integer.parseInt(request.getParameter("cardId"));
			int customerId = Integer.parseInt(request.getParameter("customerId"));
			int cardTypeId = Integer.parseInt(request.getParameter("cardTypeId"));
			String cardNumber = request.getParameter("cardNumber");
			String expiryDate = request.getParameter("expiryDate");
			String cardStatus = request.getParameter("cardStatus");

			CreditCard card = new CreditCard();
			card.setCardId(cardId);
			card.setCustomerId(customerId);
			card.setCardTypeId(cardTypeId);
			card.setCardNumber(cardNumber);
			card.setExpiryDate(java.sql.Date.valueOf(expiryDate));
			card.setStatus(CardStatus.valueOf(cardStatus));

			cardService.updateCard(card);

			response.sendRedirect(request.getContextPath() + "/card?action=list");
			return;
		}
	}
}