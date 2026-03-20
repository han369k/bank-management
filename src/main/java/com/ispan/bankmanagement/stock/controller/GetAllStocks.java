package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.dao.StockInfoDao;
import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import com.ispan.bankmanagement.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/GetAllStock")
public class GetAllStocks extends HttpServlet {
    StockInfoDao stockInfoDao = new StockInfoDao();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //拿到資料庫原始資料
        List<StockInfoEntity> daoList = stockInfoDao.GetAll();

        //完成整份清單的轉換 (Mapping) 將daoList (Stream)，全部轉成 (Map) VO，最後包成一個 List。」
        List<StockInfoDto> DtoList = daoList.stream()
                .map(StockInfoDto::ConvertEntityToDto)
                .toList();

        //DtoList 轉成 JSON
        JsonUtil.writeJson(resp, DtoList);

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
