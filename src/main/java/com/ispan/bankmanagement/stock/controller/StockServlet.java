package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.stock.service.StockInfoService;
import com.ispan.bankmanagement.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Stock")
public class StockServlet extends BaseServlet {
    StockInfoService stockInfoService = new StockInfoService();

    protected void GetAll(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setContentType("application/json;charset=UTF-8");
        // 呼叫 Service 拿資料，並透過工具類轉成 JSON 回傳
        JsonUtil.writeJson(resp, stockInfoService.GetAllStocksInfoService());
    }

    protected void Search(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String idParam = req.getParameter("stockId");//查詢參數
        int stockId = Integer.parseInt(idParam);//轉型
        //包成傳給前端的Dto
        StockInfoDto stockInfoDto = stockInfoService.GetStockInfoService(stockId);

        //轉成json格式
        JsonUtil.writeJson(resp, stockInfoDto);
    }

    protected void Delete(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String idParam = req.getParameter("stockId");
        int stockId = Integer.parseInt(idParam);
        stockInfoService.DeleteStockInfoService(stockId);
    }

    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        StockInfoDto stockInfoDto = JsonUtil.readJson(req, StockInfoDto.class);
        stockInfoDto.setStockId(Integer.parseInt(req.getParameter("stockId")));
        stockInfoDto.setStockName(req.getParameter("stockName"));
        stockInfoService.InsertStockInfoService(stockInfoDto);
    }

    protected void update(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        StockInfoDto stockInfoDto = JsonUtil.readJson(req,StockInfoDto.class);
        stockInfoService.UpdateStockInfoService(stockInfoDto);
    }
}
