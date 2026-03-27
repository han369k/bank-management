<%@ include file="/WEB-INF/views/navbar.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>申請卡片購物車</title>

<style>
    body {
        font-family: Arial, sans-serif;
        padding: 20px;
        background-color: #f5f7fa;
    }

    h2 {
        margin-bottom: 10px;
    }



    .back-btn {
        background-color: #007bff;
    }

    .clear-btn {
        background-color: #dc3545;
    }

    .top-actions a:hover,
.top-actions button:hover {
    opacity: 0.85;
    }

    .card {
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        margin-top: 15px;
    }

    .table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 10px;
    }

    .table th, .table td {
        border-bottom: 1px solid #ddd;
        padding: 12px;
        text-align: center;
    }

    .table th {
        background-color: #f1f1f1;
    }

    .table tr:hover {
        background-color: #fafafa;
    }

    .remove-btn {
        background-color: #dc3545;
        color: white;
        padding: 5px 10px;
        border-radius: 5px;
        text-decoration: none;
    }

    .remove-btn:hover {
        background-color: #c82333;
    }

    .form-group {
        margin-bottom: 15px;
    }

    input {
        padding: 8px;
        width: 200px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }

    .submit-btn {
        background-color: #28a745;
        color: white;
        padding: 10px 18px;
        border: none;
        border-radius: 6px;
        cursor: pointer;
    }

    .submit-btn:hover {
        background-color: #218838;
    }

    .empty {
        color: #888;
        margin-top: 20px;
    }
button.remove-btn {
    border: none;
    cursor: pointer;
}
    .card {
        background: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        margin-top: 15px;
    }

.top-actions button {
    margin-right: 10px;
    padding: 6px 12px;
    border-radius: 5px;
    text-decoration: none;
    color: white;
    border: none;
    cursor: pointer;
    vertical-align: middle;
}

.top-actions a,
.top-actions button {
    margin-right: 10px;
    padding: 6px 12px;
    border-radius: 5px;
    text-decoration: none;
    color: white;
    border: none;
    cursor: pointer;

    /* 🔥 關鍵三個 */
    font-size: 14px;
    font-family: inherit;
    line-height: 1.5;
}

</style>

</head>
<body>

<h2>🛒 申請卡片購物車</h2>

<div class="top-actions">
    <a class="back-btn" href="${pageContext.request.contextPath}/cardType?action=list">← 繼續選卡</a>
    <!-- <a class="clear-btn" href="${pageContext.request.contextPath}/cardCart?action=clear">清空購物車</a> -->
    <form action="${pageContext.request.contextPath}/cardCart" method="post" style="display:inline;">
        <input type="hidden" name="action" value="clear">
        <button class="clear-btn" type="submit">清空購物車</button>
    </form>



</div>

<hr>

<c:if test="${empty cartList}">
    <p class="empty">購物車目前是空的。</p>
</c:if>

<c:if test="${not empty cartList}">
    <div class="card">
        <table class="table">
            <tr>
                <th>ID</th>
                <th>卡別名稱</th>
                <th>品牌</th>
                <th>年費</th>
                <th>回饋率</th>
                <th>操作</th>
            </tr>

            <c:forEach var="item" items="${cartList}">
                <tr>
                    <td>${item.cardTypeId}</td>
                    <td>${item.cardTypeName}</td>
                    <td>${item.brand}</td>
                    <td>${item.annualFee}</td>
                    <td>${item.cashbackRate}%</td>
                    <td>
                        <!-- <a class="remove-btn"
                           href="${pageContext.request.contextPath}/cardCart?action=remove&cardTypeId=${item.cardTypeId}">
                            移除
                        </a> -->
                        <form action="${pageContext.request.contextPath}/cardCart" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="cardTypeId" value="${item.cardTypeId}">
                            <button class="remove-btn" type="submit">移除</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <div class="card">
        <h3>📋 填寫申請資料</h3>

        <form action="${pageContext.request.contextPath}/cardCart" method="post">
            <input type="hidden" name="action" value="checkout">

            <div class="form-group">
                客戶ID：<br>
                <input type="number" name="customerId" required>
            </div>

            <!-- <div class="form-group">
                預設額度：<br>
                <input type="number" name="creditLimit" step="0.01" required> -->
            <!-- </div> -->

            <button class="submit-btn" type="submit">送出申請</button>
        </form>
    </div>
</c:if>

</body>
</html>