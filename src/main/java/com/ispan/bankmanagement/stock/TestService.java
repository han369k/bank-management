package com.ispan.bankmanagement.stock;

import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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

    }
}
