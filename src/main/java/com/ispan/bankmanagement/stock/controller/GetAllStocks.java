package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.dao.StockInfoDao;
import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import com.ispan.bankmanagement.stock.service.StockInfoService;
import com.ispan.bankmanagement.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetAllStock")
public class GetAllStocks extends HttpServlet {
    StockInfoDao stockInfoDao = new StockInfoDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        StockInfoService stockInfoService = new StockInfoService();

        //回傳出來的List 轉成 JSON
        JsonUtil.writeJson(resp, stockInfoService.GetAllStocksInfoService());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
