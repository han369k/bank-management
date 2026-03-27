package com.ispan.bankmanagement.card.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ispan.bankmanagement.card.service.CardService;
import com.ispan.bankmanagement.card.service.CardTypeService;
import com.ispan.bankmanagement.card.vo.CardTypes;
import com.ispan.bankmanagement.card.vo.CreditCard;
import com.ispan.bankmanagement.card.vo.Enum.CardStatus;

@WebServlet("/cardCart")
public class CartController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private CardTypeService cardTypeService = new CardTypeService();
	private CardService cardService = new CardService();
	
	@SuppressWarnings("unchecked")
	private List<CardTypes> getCart(HttpServletRequest request) {
        List<CardTypes> cart = (List<CardTypes>) request.getSession().getAttribute("cart");
        //如果沒有購物車，建一個新的
        if (cart == null) {
            cart = new ArrayList<>();
            request.getSession().setAttribute("cart", cart);
        }
        return cart;
    }
	
       
    public CartController() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = request.getParameter("action");
        
        if (action == null || "view".equals(action)) {
            viewCart(request, response);
            return;
        }
      //防呆
        response.sendRedirect(request.getContextPath() + "/cardCart?action=view");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");

		if ("add".equals(action)) {
			addToCart(request, response);
			return;
		}
		
		if ("remove".equals(action)) {
			removeFromCart(request, response);
			return;
		}
		
		if ("clear".equals(action)) {
			request.getSession().removeAttribute("cart");
			response.sendRedirect(request.getContextPath() + "/cardCart?action=view");
			return;
		}
		

		
		if ("checkout".equals(action)) {
            checkout(request, response);
            return;
        }
        //防呆
        response.sendRedirect(request.getContextPath() + "/cardCart?action=view");
	}
	
	//新增至購物車
	private void addToCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String cardTypeIdStr=request.getParameter("cardTypeId");
		if (cardTypeIdStr == null || cardTypeIdStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/cardType?action=list");
            return;
        }
		int cardTypeId = Integer.parseInt(cardTypeIdStr);
		CardTypes type = cardTypeService.getCardTypeById(cardTypeId);
		if (type == null) {
            response.sendRedirect(request.getContextPath() + "/cardType?action=list");
            return;
        }
		List<CardTypes> cart = getCart(request);
		boolean exists = cart.stream()
                .anyMatch(item -> item.getCardTypeId() == cardTypeId);
		//不存在才加
		if (!exists) {
            cart.add(type);
        }
		request.getSession().setAttribute("cart", cart);
		response.sendRedirect(request.getContextPath() + "/cardType?action=list");
	}
	//移除購物車
	private void removeFromCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String cardTypeIdStr = request.getParameter("cardTypeId");
		if (cardTypeIdStr != null && !cardTypeIdStr.isBlank()) {
            int cardTypeId = Integer.parseInt(cardTypeIdStr);
            List<CardTypes> cart = getCart(request);
            cart.removeIf(item -> item.getCardTypeId() == cardTypeId);
            request.getSession().setAttribute("cart", cart);
        }
		response.sendRedirect(request.getContextPath() + "/cardCart?action=view");
	}
	//看購物車
	private void viewCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<CardTypes> cart = getCart(request);
		request.setAttribute("cartList", cart);
		request.getRequestDispatcher("/WEB-INF/views/creditCard/cart.jsp")
        .forward(request, response);	
	}
	//購物車結帳
	private void checkout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String customerIdStr = request.getParameter("customerId");
        
		
        List<CardTypes> cart = getCart(request);
        if (cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cardCart?action=view");
            return;
        }

        if (customerIdStr == null || customerIdStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/cardCart?action=view");
            return;
        }

        int customerId = Integer.parseInt(customerIdStr);
        
        for (CardTypes type : cart) {
            CreditCard card = new CreditCard();
            card.setCustomerId(customerId);
            card.setCardTypeId(type.getCardTypeId());
            card.setCardNumber(generateCardNumber());
            card.setExpiryDate(Date.valueOf(LocalDate.now().plusYears(4)));
            card.setCreditLimit(BigDecimal.ZERO);// 不由使用者輸入
            card.setCurrentBalance(BigDecimal.ZERO);
            card.setStatus(CardStatus.INACTIVE);//預設是
            

            cardService.addCard(card);
        }
        System.out.println("customerId = " + customerId);
        request.getSession().removeAttribute("cart");
        //回到卡片list
        response.sendRedirect(request.getContextPath() + "/cardType?action=list");
	}
	private String generateCardNumber() {
		long n = Math.abs(java.util.concurrent.ThreadLocalRandom.current()
                .nextLong(0, 1_000_000_000_000L));//產生12位數，不到12位數補0
        return String.format("4567%012d", n);
	}
	
	
}
