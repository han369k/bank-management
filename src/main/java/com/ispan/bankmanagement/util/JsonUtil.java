package com.ispan.bankmanagement.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void writeJson(HttpServletResponse resp, Object data) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            // 直接把物件丟進去，Jackson 會自動處理
            mapper.writeValue(resp.getWriter(), data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 轉換失敗", e);
        }
    }
}