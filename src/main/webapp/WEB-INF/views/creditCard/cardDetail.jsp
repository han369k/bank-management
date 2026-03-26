<%@ include file="/WEB-INF/views/navbar.jsp" %>
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

<!-- 帳單資訊 -->
<p>帳單ID：${bill.billId}</p>
<p>卡號ID：${bill.cardId}</p>
<p>帳單月份：${bill.billingMonth}</p>
<p>總金額：${bill.totalAmount}</p>
<p>已繳：${bill.paidAmount}</p>
<p>剩餘：${bill.totalAmount - bill.paidAmount}</p>

<hr>

<h3>交易明細</h3>

<table border="1">
<tr>
    <th>交易ID</th>
    <th>商家ID</th>
    <th>金額</th>
    <th>類型</th>
    <th>時間</th>
    <th>備註</th>
</tr>

<c:forEach var="txn" items="${details}">
<tr>
    <td>${txn.txnId}</td>
    <td>${txn.merchantId}</td>

    <!-- 金額顏色 -->
    <td>
        <c:choose>
            <c:when test="${txn.txnAmount < 0}">
                <span style="color:green;">${txn.txnAmount}</span>
            </c:when>
            <c:otherwise>
                <span style="color:red;">${txn.txnAmount}</span>
            </c:otherwise>
        </c:choose>
    </td>

    <td>${txn.txnType}</td>
    <td>${txn.txnDate}</td>
    <td>${txn.description}</td>
</tr>
</c:forEach>

</table>

<br>

<a href="${pageContext.request.contextPath}/bill">返回帳單列表</a>    
</body>
</html>