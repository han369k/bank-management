package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.stock.service.StockInfoService;
import com.ispan.bankmanagement.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/UpdateStock")
public class UpdateStock extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StockInfoDto stockInfoDto = JsonUtil.readJson(req,StockInfoDto.class);
        StockInfoService stockInfoService = new StockInfoService();
        stockInfoService.UpdateStockInfoService(stockInfoDto);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
