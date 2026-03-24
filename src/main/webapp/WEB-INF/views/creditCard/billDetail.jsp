<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <h2>帳單明細</h2>
<p>帳單月份：${month}</p>
<p>總金額：${calculatedTotal}</p>
<p>已繳：0</p>
<p>剩餘：${calculatedRemaining}</p>
<hr>
<form id="filterForm" method="get" action="${pageContext.request.contextPath}/bill">
    <input type="hidden" name="action" value="detail">
    <input type="hidden" name="month" value="${month}">
    <input type="hidden" name="customerId" value="${customerId}">

    <label>選擇卡片：</label>
    <select name="cardId" id="cardSelect">
        <option value=""
        <c:if test="${empty selectedCardId}">selected</c:if>>
        全部卡片
    </option>

        <c:forEach var="card" items="${cardList}">
            <option value="${card.cardId}"
                <c:if test="${selectedCardId eq card.cardId}">selected</c:if>>
                
                <!-- 卡號遮罩 -->
                **** **** **** ${card.cardNumber.substring(card.cardNumber.length()-4)}
            </option>
        </c:forEach>
    </select>
</form>
<!-- 交易明細 -->
<table border="1">
    <tr>
        <th>日期</th>
        <th>描述</th>
        <th>金額</th>
    </tr>

    <c:forEach var="tx" items="${details}">
        <tr>
            <td>${tx.txnDate}</td>
            <td>${tx.description}</td>
            <td>${tx.txnAmount}</td>
        </tr>
    </c:forEach>
</table>

<br>
<a href="${pageContext.request.contextPath}/bill">返回帳單列表</a>



    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const cardSelect = document.getElementById("cardSelect");
            const form = document.getElementById("filterForm");
            
            cardSelect.addEventListener("change", function() {
                form.submit();
            });
        });
    </script>
</body>
</html>