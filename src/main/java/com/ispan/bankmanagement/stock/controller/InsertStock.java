package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import com.ispan.bankmanagement.stock.service.StockInfoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.ispan.bankmanagement.stock.entity.StockInfoEntity.ConvertDtoToEntity;

@WebServlet("/InsertStock")
public class InsertStock extends HttpServlet {
    StockInfoDto stockInfoDto = new StockInfoDto();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=UTF-8");

        stockInfoDto.setStockId(Integer.parseInt(req.getParameter("stockId")));
        stockInfoDto.setStockName(req.getParameter("stockName"));

        StockInfoService stockInfoService = new StockInfoService();
        stockInfoService.InsertStockInfoService(stockInfoDto);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }
}
