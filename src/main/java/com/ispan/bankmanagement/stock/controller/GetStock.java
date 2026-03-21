package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.dao.StockInfoDao;
import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static com.ispan.bankmanagement.stock.dto.StockInfoDto.ConvertEntityToDto;

@WebServlet("/GetStock")
public class GetStock extends HttpServlet {
    StockInfoDao stockInfoDao = new StockInfoDao();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("stockId");//查詢參數
        int stockId = Integer.parseInt(idParam);//轉型
        StockInfoDto stockInfoDto = null;//執行SQL 包成傳給前端的Dto
        try {
            stockInfoDto = StockInfoDto.ConvertEntityToDto(stockInfoDao.GetById(stockId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        JsonUtil.writeJson(resp, stockInfoDto);//轉成json格式
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
