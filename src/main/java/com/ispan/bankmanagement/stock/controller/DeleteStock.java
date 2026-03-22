package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.service.StockInfoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/DeleteStock")
public class DeleteStock extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=UTF-8");
        int stockId = Integer.parseInt(req.getParameter("stockId"));
        StockInfoService stockInfoService = new StockInfoService();
        stockInfoService.DeleteStockInfoService(stockId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
