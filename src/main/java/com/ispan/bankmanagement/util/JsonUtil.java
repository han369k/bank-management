package com.ispan.bankmanagement.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
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

    // 1. 轉換為單一物件 (傳入 Class<T>)
    public static <T> T readJson(HttpServletRequest req, Class<T> clazz) {
        try {
            return mapper.readValue(req.getInputStream(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 反序列化失敗", e);
        }
    }

    // 2. 轉換為複雜泛型 (如 List<class>, Map<String, class>)
    public static <T> T readJson(HttpServletRequest req, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(req.getInputStream(), typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 泛型反序列化失敗", e);
        }
    }

    public static int readJson(HttpServletRequest req) {
        try {
            JsonNode rootNode = mapper.readTree(req.getInputStream());
            return rootNode.path("stockId").asInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}