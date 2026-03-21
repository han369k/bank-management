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

//測試Service用
@WebServlet("/TestService")
public class TestService extends HttpServlet {
    StockInfoDto stockInfoDto = new StockInfoDto();
    StockInfoDto stockInfoDto2 = new StockInfoDto();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=UTF-8");
        stockInfoDto.setStockId(Integer.parseInt(req.getParameter("stockId")));
        stockInfoDto.setStockName(req.getParameter("stockName"));
        StockInfoService stockInfoService = new StockInfoService();
        stockInfoDto2.setStockId(Integer.parseInt(req.getParameter("stockId"))+1);
        stockInfoDto2.setStockName(req.getParameter("stockName")+"_copy");
        StockInfoEntity stockInfoEntity = ConvertDtoToEntity(stockInfoDto);
        StockInfoEntity stockInfoEntity2 = ConvertDtoToEntity(stockInfoDto2);
        stockInfoService.UpdateTwoStockInfoService(stockInfoEntity, stockInfoEntity2);
    }
}
