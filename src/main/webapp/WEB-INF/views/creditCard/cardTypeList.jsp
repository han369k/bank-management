<%@ include file="/WEB-INF/views/navbar.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>卡片種類列表</title>

<style>
    body {
        font-family: Arial, sans-serif;
        padding: 20px;
    }

    h2 {
        margin-bottom: 10px;
    }

    a {
        text-decoration: none;
        color: #007bff;
    }

    a:hover {
        text-decoration: underline;
    }

    .btn {
        padding: 6px 12px;
        background-color: #28a745;
        color: white;
        border-radius: 5px;
        text-decoration: none;
    }

    .btn:hover {
        background-color: #218838;
    }

    .table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 15px;
    }

    .table th, .table td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: center;
    }

    .table th {
        background-color: #f4f4f4;
    }

    .table tr:hover {
        background-color: #f9f9f9;
    }

</style>

</head>
<body>

    <h2>卡片種類列表</h2>

    <a href="${pageContext.request.contextPath}/cardCart?action=view">🛒 查看購物車</a>
    <hr>

    <table class="table">
        <tr>
            <th>ID</th>
            <th>卡別名稱</th>
            <th>品牌</th>
            <th>年費</th>
            <th>回饋率</th>
            <th>操作</th>
        </tr>

        <c:forEach var="type" items="${cardTypeList}">
            <tr>
                <td>${type.cardTypeId}</td>
                <td>${type.cardTypeName}</td>
                <td>${type.brand}</td>
                <td>${type.annualFee}</td>
                <td>${type.cashbackRate}%</td>
                <td>
                    <a class="btn"
                       href="${pageContext.request.contextPath}/cardCart?action=add&cardTypeId=${type.cardTypeId}">
                        加入購物車
                    </a>
                </td>
            </tr>
        </c:forEach>

    </table>

</body>
</html>