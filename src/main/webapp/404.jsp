<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <title>404 - 迷路啦</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; display: flex; align-items: center; justify-content: center; height: 100vh; }
        .error-card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); text-align: center; max-width: 500px; }
        .debug-box { background: #e9ecef; padding: 15px; border-radius: 5px; text-align: left; margin-top: 20px; font-family: monospace; font-size: 14px; color: #dc3545;}
    </style>
</head>
<body>

<div class="error-card">
    <h1 class="display-1 fw-bold text-danger">404</h1>
    <h3 class="mb-3">Oops! 找不到網頁 🕵️‍♂️</h3>
    <p class="text-muted">抱歉，您要找的頁面可能被外星人綁架了，或是路徑根本打錯了！</p>

    <div class="debug-box">
        <strong>[Debug 系統提示]</strong><br>
        目標路徑：<%= request.getAttribute("javax.servlet.error.request_uri") != null ? request.getAttribute("javax.servlet.error.request_uri") : request.getRequestURI() %>
    </div>

    <a href="<%= request.getContextPath() %>/view/index.html" class="btn btn-dark mt-4">🏠 帶我回首頁</a>
</div>

</body>
</html>