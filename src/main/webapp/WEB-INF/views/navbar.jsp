<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<div style="
    background:#2c3e50;
    padding:12px 20px;
    display:flex;
    justify-content:space-between;
    align-items:center;
    color:white;
">

    <!-- 左邊 -->
    <div>
        <a href="${pageContext.request.contextPath}/creditCardHome"
           style="color:white; text-decoration:none; margin-right:15px;">
           💳 信用卡首頁
        </a>

        <a href="${pageContext.request.contextPath}/cardCart"
           style="color:white; text-decoration:none;">
           🛒 購物車
        </a>
    </div>

    <!-- 右邊 -->
    <div>
        ${sessionScope.user != null ? "歡迎, " += sessionScope.user.name : ""}
    </div>

</div>
</body>
</html>