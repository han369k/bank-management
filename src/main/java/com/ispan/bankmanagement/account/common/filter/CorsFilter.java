package com.ispan.bankmanagement.account.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 處理 CORS (Cross-Origin Resource Sharing) 跨域請求的過濾器。
 * 當前端（例如跑在 localhost:3000）與後端（跑在 localhost:8080）不同源時，
 * 瀏覽器會發送一個 "preflight" OPTIONS 請求來確認是否允許跨域，此 Filter 負責回應它。
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 設置允許跨域的來源、方法和標頭
        httpResponse.setHeader("Access-Control-Allow-Origin", "*"); // 允許任何來源，在生產環境中應更換為指定的前端網域
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // 允許的 HTTP 方法
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization"); // 允許的請求標頭

        // 如果是 preflight OPTIONS 請求，直接回傳 200 OK，不需要往下執行
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 對於非 OPTIONS 請求，繼續執行後續的 Filter 或 Servlet
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Filter 初始化，此處無需特殊處理
    }

    @Override
    public void destroy() {
        // Filter 銷毀，此處無需特殊處理
    }
}
