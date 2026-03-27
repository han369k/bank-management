<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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

        <a href="${pageContext.request.contextPath}/cardCart/view"
           style="color:white; text-decoration:none;">
           🛒 購物車<span id="cartCount">${empty sessionScope.cart ? 0 : sessionScope.cart.size()}</span>
        </a>
    </div>

    <!-- 右邊 -->
    <div>
        <c:if test="${sessionScope.user != null}">
            歡迎, ${sessionScope.user.name}
        </c:if>
    </div>

</div>
<script>
    // 模擬購物車數量更新-法2
    function updateCartCount(count) {
        document.getElementById('cartCount').textContent = count;
    }

    // 假設從後端獲取購物車數量
    // updateCartCount(3); // 這裡可以替換成實際的數量
</script>
</body>
</html>