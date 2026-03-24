package com.ispan.bankmanagement.stock.controller;

import com.ispan.bankmanagement.stock.service.StockInfoService;
import com.ispan.bankmanagement.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class BaseServlet extends HttpServlet {
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 統一設定編碼
        req.setCharacterEncoding("UTF-8");
        String methodName = req.getParameter("method");
        try {
            // 找到子類別(this)中與 methodName 同名的方法
            Method method = this.getClass().getDeclaredMethod(methodName,
                    HttpServletRequest.class, HttpServletResponse.class);

            // 檢查特定敏感功能
            if ("Delete".equals(methodName) || "Update".equals(methodName) || "Insert".equals(methodName)) {
                if (!"POST".equalsIgnoreCase(req.getMethod())) {
                    resp.sendError(405, "不允許使用 GET 執行此動作");
                    return;
                }
            }
            // 執行它
            method.invoke(this, req, resp);
        } catch (Exception e) {
            resp.sendError(500, "執行功能時發生錯誤");
        }
    }

}
